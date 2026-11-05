package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*

/** Bounded in-memory idempotency for a preview session. K16 must persist the request envelope
 * and compare versions in its write transaction; restarting this session does not persist plans. */
class WeeklyPlanSession(private val maximumRequests: Int = 64) {
    init { require(maximumRequests > 0) }
    private data class Payload(val request: PlanGenerationRequest, val profile: UserProfile,
                               val policy: NutritionPolicy?, val data: WeeklyPlanningData)
    private val requests = mutableMapOf<String, Pair<Payload, PlanGenerationResult>>()

    @Synchronized
    fun generate(q: PlanGenerationRequest, profile: UserProfile, policy: NutritionPolicy?,
                 data: WeeklyPlanningData): PlanGenerationResult = execute(q, profile, policy, data, false)

    @Synchronized
    internal fun generateTest(q: PlanGenerationRequest, profile: UserProfile, policy: NutritionPolicy?,
                              data: WeeklyPlanningData): PlanGenerationResult = execute(q, profile, policy, data, true)

    private fun execute(q: PlanGenerationRequest, profile: UserProfile, policy: NutritionPolicy?,
                        data: WeeklyPlanningData, test: Boolean): PlanGenerationResult {
        val payload = Payload(q, profile, policy, data)
        val previous = requests[q.requestId]
        if (previous != null && previous.first == payload && previous.second.testOnly == test) return previous.second
        val conflict = previous != null
        if (conflict || requests.size >= maximumRequests) return PlanGenerationResult(
            if (conflict) GenerationStatus.REQUEST_CONFLICT else GenerationStatus.TEMPORARY_FAILURE, test, null,
            listOf(CandidateRuleResult("request_id", q.candidate.policy, "request", "same payload", null, null, null,
                RuleStatus.FAIL, emptyList(), emptyList(), if (conflict) "Request ID already belongs to a different payload"
                else "Preview session capacity reached")), 0)
        val result = if (test) WeeklyPlanGenerator.generateTest(q, profile, policy, data)
            else WeeklyPlanGenerator.generate(q, profile, policy, data)
        requests[q.requestId] = payload to result
        return result
    }
}
