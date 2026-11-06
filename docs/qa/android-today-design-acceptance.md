# Today illustrated dashboard

Scope: approved simple-turquoise/00-reference Today direction and §18.11/19.6/19.12,
with existing accepted-plan recipe details and saved-log editor reused.

- Turquoise illustrated hero, compact actual-intake summary, illustrated plan status
  shortcuts and clickable planned/recorded cards. Large fonts wrap and scroll.
- Accepted plans/current date only; names resolve from exact recipe version. No
  draft/reviewed catalog promotion or synthetic production meals.
- Generic approved artwork is explicitly labelled illustration; it is not a real
  meal photograph or measured portion. No hardcoded example calories/name/avatar.
- Actual diary entries and accepted plans remain separate. No write occurs on
  viewing a card. Recipe Yedim still requires portion confirmation.
- Saved snapshot nutrients (manual/recipe keys), edit and delete are in saved-log details; Back returns to
  dashboard, mutation refreshes it, recipe return reloads plan/log data.
- Remaining-energy number stays unavailable: current diary has no completeness
  declaration. Missing nutrient data remains unknown, never zero.
- Existing tokens, 48dp targets and native accessible card actions reused. Large-font
  plan shortcuts expand to full width; system and on-screen recipe Back refresh data.

Acceptance checks: TodayDashboardUiTest renders AZ at 100% and AZ/EN/RU at 150%,
opens exact plan/log callbacks into existing details without confirmation, and
covers empty-day add. FoodEntryUiTest covers persisted edit → dashboard card →
existing detail → delete → empty/recreation. Mandatory lint, Kotlin compilation,
JVM tests and token/contrast checks run only on merged main via local finish.
Final pass/failure is .local-ci/logs/latest.json for the merged commit.

Visual evidence: native JVM PNGs build/reports/k10/today-dashboard-* and
k12-today-az.png; inspected images and any remaining limits are recorded in the
context handoff. No APK, install, device/TalkBack or real production catalog E2E
is claimed. Generic art and native type are deliberate adaptations, not pixel
identity with the concept image.
