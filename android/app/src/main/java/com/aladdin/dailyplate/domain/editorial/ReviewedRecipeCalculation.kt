package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.VersionRef
import kotlinx.serialization.Serializable

@Serializable
enum class ReviewedComponentState { RAW, ALREADY_COOKED, AS_DESCRIBED }

/** Ordered one-to-one with recipe ingredients. Factors are supplied evidence, never inferred. */
@Serializable
data class ReviewedComponentCalculation(
    val food: VersionRef, val state: ReviewedComponentState,
    val source: String, val retainedFraction: Double,
    val nutrientRetention: Map<String, Double>,
)

@Serializable
enum class ReviewedYieldBasis { MEASURED, DOCUMENTED_ESTIMATE }

/** Included inside the hashed payload, so calculation changes require a new reviewed version. */
@Serializable
data class ReviewedRecipeCalculation(
    val method: String,
    val components: List<ReviewedComponentCalculation>,
    val yieldBasis: ReviewedYieldBasis? = null,
)
