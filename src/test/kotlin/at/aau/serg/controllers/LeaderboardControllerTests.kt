package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.Mockito.`when` as whenever

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = LeaderboardController(mockedService)
    }

    @Test
    // Leaderboard mit Test-Werten
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res = controller.getLeaderboard(null)

        // Prüfen, ob korrekt nach Score sortiert wurde
        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_correctTimeSorting() {
        // Alle drei haben denselben Score: kleinere Zeit = besserer Rang.
        val slower = GameResult(1, "slower", 20, 20.0)
        val faster = GameResult(2, "faster", 20, 10.0)
        val middle = GameResult(3, "middle", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(middle, slower, faster))

        val res = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        // Erwartete Reihenfolge: 10.0 Sekunden, dann 15.0, dann 20.0
        assertEquals(3, res.size)
        assertEquals(faster, res[0])
        assertEquals(middle, res[1])
        assertEquals(slower, res[2])
    }

    @Test
    fun test_getLeaderboard_withRank_returnsPlayerAndThreeNeighbors() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        val p2 = GameResult(2, "p2", 90, 10.0)
        val p3 = GameResult(3, "p3", 80, 10.0)
        val p4 = GameResult(4, "p4", 70, 10.0)
        val p5 = GameResult(5, "p5", 60, 10.0)
        val p6 = GameResult(6, "p6", 50, 10.0)
        val p7 = GameResult(7, "p7", 40, 10.0)
        val p8 = GameResult(8, "p8", 30, 10.0)

        // Absichtlich unsortiert zurückgegeben
        whenever(mockedService.getGameResults()).thenReturn(listOf(p8, p6, p4, p2, p7, p1, p5, p3))

        val res = controller.getLeaderboard(5)

        // Erwartet werden Platz 2 bis Platz 8
        assertEquals(listOf(p2, p3, p4, p5, p6, p7, p8), res)
    }

    @Test
    fun test_getLeaderboard_withRank_nearStart_returnsAvailableWindowOnly() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        val p2 = GameResult(2, "p2", 90, 10.0)
        val p3 = GameResult(3, "p3", 80, 10.0)
        val p4 = GameResult(4, "p4", 70, 10.0)
        val p5 = GameResult(5, "p5", 60, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(p5, p3, p1, p4, p2))


        val res = controller.getLeaderboard(1)

        // check rank = 1: es gibt keine drei Einträge davor, also nur die verfügbaren nachfolgenden mitliefern
        assertEquals(listOf(p1, p2, p3, p4), res)
    }

    @Test
    fun test_getLeaderboard_withRank_tooLarge_throwsBadRequest() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(p1))

        // rank = 2 ist ungültig, weil nur ein Eintrag existiert
        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(2)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
    }

    @Test
    fun test_getLeaderboard_withRank_zero_throwsBadRequest() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(p1))

        // rank = 0 ist ebenfalls ungültig
        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(0)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
    }
}