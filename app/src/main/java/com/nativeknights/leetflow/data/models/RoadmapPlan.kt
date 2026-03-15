import kotlinx.serialization.Serializable

@Serializable
data class RoadmapPlan(
    val topic: String,
    val patterns: List<PatternGroup>,
    val stats: RoadmapStats
)

@Serializable
data class PatternGroup(
    val patternName: String,
    val difficulty: String,
    val description: String,
    val keyConcepts: List<String>,
    val realWorldUse: String,
    val companiesAskThis: String,
    val estimatedProblems: String,
    val mustKnow: Boolean
)

@Serializable
data class RoadmapStats(
    val totalPatterns: Int,
    val mustKnowPatterns: Int,
    val estimatedWeeks: String
)