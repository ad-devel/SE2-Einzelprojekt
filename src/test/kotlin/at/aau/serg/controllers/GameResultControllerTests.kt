package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever

class GameResultControllerTests {

    // Service mocken, Controller-Methoden isoliert testen
    private lateinit var mockedService: GameResultService

    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult_returnsServiceResult() {
        // vorhandenes GameResult
        val gameResult = GameResult(1, "player1", 17, 15.3)

        // Service soll dieses Objekt zurückgeben
        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val res = controller.getGameResult(1)

        // Prüfen, ob delegiert wurde
        verify(mockedService).getGameResult(1)

        // Prüfen, ob die Rückgabe des Controllers mit der des Services übereinstimmt
        assertEquals(gameResult, res)
    }

    @Test
    fun test_getGameResult_returnsNull_whenServiceReturnsNull() {
        // nicht vorhandene ID simulieren
        whenever(mockedService.getGameResult(99)).thenReturn(null)

        val res = controller.getGameResult(99)

        verify(mockedService).getGameResult(99)
        assertEquals(null, res)
    }

    @Test
    fun test_getAllGameResults_returnsAllElements() {
        val first = GameResult(1, "player1", 17, 15.3)
        val second = GameResult(2, "player2", 25, 11.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(first, second))

        val res = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(listOf(first, second), res)
    }

    @Test
    fun test_addGameResult_delegatesToService() {
        val gameResult = GameResult(0, "player1", 17, 15.3)

        // Die Methode gibt nichts zurück, prüfen mit verify, ob Service korrekt aufgerufen wurde.
        controller.addGameResult(gameResult)

        verify(mockedService).addGameResult(gameResult)
    }

    @Test
    fun test_deleteGameResult_delegatesToService() {
        controller.deleteGameResult(5)

        verify(mockedService).deleteGameResult(5)
    }
}