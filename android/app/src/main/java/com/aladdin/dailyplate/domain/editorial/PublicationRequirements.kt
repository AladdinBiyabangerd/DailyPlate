package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*

/** Completeness checks, not clinical approval or a replacement for K04 calculation. */
object PublicationRequirements {
    fun missing(content: EditorialContent, m: EditorialMetadata): Set<String> = buildSet {
        fun check(code: String, valid: Boolean) { if (!valid) add(code) }
        check("sources", m.sourceRefs.present())
        check("text_rights", !m.textRights.isNullOrBlank())
        check("translations", setOf("az", "en", "ru").all { !m.translationChecks[it].isNullOrBlank() })
        when (content) {
            is EditorialContent.RecipeContent -> {
                val r = content.recipe
                check("names", r.names.complete())
                check("ingredients", !m.ingredientCheck.isNullOrBlank())
                check("allergens", r.allergens != null && !m.allergenCheck.isNullOrBlank())
                check("instructions", r.cookingMethod.isNotBlank() && r.instructions.present() && !m.instructionCheck.isNullOrBlank())
                check("nutrition", r.nutrientsTotal.values.isNotEmpty() && !m.nutritionCheck.isNullOrBlank())
                check("servings", m.declaredServings?.let { it.isFinite() && it > 0 } == true)
                check("time", r.time.totalMinutes != null)
                val s = m.storage
                check("storage", s != null && s.id.isNotBlank() && s.id == r.storageInstructionId &&
                    s.cooling.complete() && s.refrigeration.complete() && s.freezing.complete() &&
                    s.reheating.complete() && s.sourceRefs.present())
                check("rice_storage", m.containsRice != null && (m.containsRice != true || !s?.riceSpecificSource.isNullOrBlank()))
                // Neither yield nor photos is a universal prerequisite for per-serving estimates.
                check("yield", r.yieldCookedGrams == null || !m.yieldEvidence.isNullOrBlank())
                check("cooked_100g", r.nutrientsPer100g == null || (r.yieldCookedGrams != null && !m.yieldEvidence.isNullOrBlank()))
                check("photo_calibration", r.portionPhotos.isEmpty() || (r.portionRange != null &&
                    r.portionPhotos.all { (key, photo) ->
                        val c = m.photoCalibrations[key]
                        c != null && photo.isNotBlank() && !m.photoRights[photo].isNullOrBlank() &&
                            c.grams >= r.portionRange.minGrams && c.grams <= r.portionRange.maxGrams
                    }))
                check("price_source", r.priceEstimate == null || (!r.costRegion.isNullOrBlank() && r.costDate != null))
            }
            is EditorialContent.PolicyContent -> {
                val p = content.policy
                check("clinical_scope", p.eligiblePopulation.isNotBlank() && !m.clinicalScopeCheck.isNullOrBlank())
                check("policy_inputs", p.requiredInputs.isNotEmpty() && p.requiredInputs.all { it.isNotBlank() })
                check("activity", p.activityMapping.isNotEmpty() && p.activityMapping.keys.all { it.isNotBlank() })
                check("policy_evidence", p.evidenceRefs.present())
                check("goal_rules", p.energyAdjustment.keys.containsAll(Goal.entries))
                check("policy_rules", listOf(p.exclusionRules, p.energyBounds, p.macroRules, p.nutrientChecks,
                    p.reassessmentRules, p.energyAdjustment.values.toList()).all { rules ->
                    rules.isNotEmpty() && rules.all { it.code.isNotBlank() && it.evidenceRefs.present() &&
                        it.parameters.isNotEmpty() && it.parameters.all { (k, v) -> k.isNotBlank() && v.isNotBlank() } }
                })
            }
            is EditorialContent.EvidenceContent -> {
                val c = content.card
                check("claim", c.claimId.isNotBlank() && c.text.complete())
                check("source_section", (c.sourceUrl.startsWith("https://") || c.sourceUrl.startsWith("http://")) && c.sourceSection.isNotBlank())
                check("scope", c.eligiblePopulation.isNotBlank() && c.limitations.complete())
                check("next_review", m.nextReviewDate != null)
            }
        }
    }

    private fun List<String>.present() = isNotEmpty() && all { it.isNotBlank() }
    private fun LocalizedText.complete() = az.isNotBlank() && !en.isNullOrBlank() && !ru.isNullOrBlank()
}
