package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*

internal val editorTime = RecordedAt("2026-11-04T10:00:00Z", "Asia/Baku")
internal val testText = LocalizedText("Test AZ", "Test EN", "Test RU")
internal val testRoles: (String) -> Set<EditorialRole> = { actor -> when (actor) {
    "author" -> setOf(EditorialRole.AUTHOR)
    "reviewer" -> setOf(EditorialRole.CLINICAL_REVIEWER)
    "publisher" -> setOf(EditorialRole.PUBLISHER)
    "both" -> setOf(EditorialRole.AUTHOR, EditorialRole.CLINICAL_REVIEWER)
    else -> emptySet()
} }
internal val completeMetadata = EditorialMetadata(
    sourceRefs = listOf("TEST source, not clinical evidence"), textRights = "TEST text permission",
    translationChecks = mapOf("az" to "test-az", "en" to "test-en", "ru" to "test-ru"),
    ingredientCheck = "TEST mapping", allergenCheck = "TEST review", instructionCheck = "TEST method",
    nutritionCheck = "TEST calculation", declaredServings = 4.0,
    storage = StorageGuidance("test-storage", testText, testText, testText, testText, listOf("TEST safety source")),
    containsRice = false, clinicalScopeCheck = "TEST only scope", nextReviewDate = CalendarDate("2027-01-01"),
)
internal fun editorialRecipe() = EditorialContent.RecipeContent(Recipe(
    VersionRef("recipe", 7), testText, listOf(Ingredient(VersionRef("food", 1), 100.0)),
    "TEST boil", listOf("TEST instruction"), null, null,
    NutrientSnapshot(mapOf("energy" to NutrientAmount(100.0, NutrientUnit.KCAL),
        "protein" to NutrientAmount(null, NutrientUnit.GRAM))),
    null, null, emptyMap(), emptySet(), setOf("pot"), PreparationTime(20, 10, 10),
    "test-storage", null, null, null, null, null, ContentStatus.DRAFT,
))
internal fun editorialPolicy(): EditorialContent.PolicyContent {
    val rule = PolicyRule("TEST not a clinical rule", mapOf("fixture" to "not applicable to users"), listOf("TEST source"))
    return EditorialContent.PolicyContent(NutritionPolicy(VersionRef("policy", 1), "TEST population",
        listOf(rule), VersionRef("TEST formula", 1), setOf("TEST input"), mapOf("TEST activity" to 1.0),
        Goal.entries.associateWith { rule }, listOf(rule), listOf(rule), listOf(rule), listOf(rule),
        listOf("TEST source"), null, null, PolicyStatus.DRAFT))
}
internal fun editorialCard() = EditorialContent.EvidenceContent(EvidenceCard(
    VersionRef("card", 1), "claim-test", testText, "https://example.org/test", "TEST section",
    "TEST population", testText, null, ContentStatus.DRAFT,
))
