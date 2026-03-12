package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    // optionalen Parameter rank inkludieren
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
        val sortedLeaderboard = gameResultService.getGameResults()
            // Änderung der Sortier-Logik: höherer score besser, bei gleichem score kürzere Spieldauer berücksichtigen
            .sortedWith(compareByDescending<GameResult> { it.score }.thenBy { it.timeInSeconds })

        // wird rank nicht angegeben, gesamtes Leaderboard zurückgeben
        if (rank == null) {
            return sortedLeaderboard
        }

        // handling für ungültige rank-Werte (zu groß oder negativ, oder 0)
        if (rank <= 0 || rank > sortedLeaderboard.size) {
            throw ResponseStatusException(
                // HTTP 400
                HttpStatus.BAD_REQUEST, "Rank must be between 1 and ${sortedLeaderboard.size}"
            )
        }

        // wird rank angegeben, Spieler auf passenden Platz sowie die 3 oberen und unteren Spieler angeben
        val index = rank - 1
        val fromIndex = maxOf(0, index - 3)
        val toIndex = minOf(sortedLeaderboard.size, index + 4)

        return sortedLeaderboard.subList(fromIndex, toIndex)
    }
}