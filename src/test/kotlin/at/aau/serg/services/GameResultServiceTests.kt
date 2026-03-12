package at.aau.serg.services

import at.aau.serg.models.GameResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameResultServiceTests {

    private lateinit var service: GameResultService

    @BeforeEach
    fun setup() {
        service = GameResultService()
    }

    @Test
    fun test_getGameResults_emptyList() {
        val result = service.getGameResults()

        assertEquals(emptyList<GameResult>(), result)
    }

    @Test
    fun test_addGameResult_getGameResults_containsSingleElement() {
        val gameResult = GameResult(1, "player1", 17, 15.3)

        service.addGameResult(gameResult)
        val res = service.getGameResults()

        // nach dem Einfügen soll genau ein Eintrag vorhanden sein
        assertEquals(1, res.size)
        assertEquals(gameResult, res[0])
        assertEquals(1, res[0].id)
    }

    @Test
    fun test_getGameResultById_existingId_returnsObject() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(1)

        assertEquals(gameResult, res)
    }

    @Test
    fun test_getGameResultById_nonexistentId_returnsNull() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(22)

        assertNull(res)
    }

    @Test
    fun test_addGameResult_multipleEntries_correctId() {
        // starten mit ID 0, weil Service die ID selbst fortlaufend setzt
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        val gameResult2 = GameResult(0, "player2", 25, 16.0)

        service.addGameResult(gameResult1)
        service.addGameResult(gameResult2)

        val res = service.getGameResults()

        assertEquals(2, res.size)

        // Erstes Element soll ID 1 bekommen
        assertEquals(gameResult1, res[0])
        assertEquals(1, res[0].id)

        // Zweites Element soll ID 2 bekommen
        assertEquals(gameResult2, res[1])
        assertEquals(2, res[1].id)
    }

    @Test
    fun test_deleteGameResult_existingId_removesElement() {
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        val gameResult2 = GameResult(0, "player2", 25, 16.0)

        service.addGameResult(gameResult1)
        service.addGameResult(gameResult2)

        // löschen erstes Element über ID
        service.deleteGameResult(1)

        // nur noch zweites Element sollte vorhanden sein
        val res = service.getGameResults()
        assertEquals(1, res.size)
        assertEquals(gameResult2, res[0])
    }

    @Test
    fun test_deleteGameResult_nonexistentId_keepsListUnchanged() {
        val gameResult = GameResult(0, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        // Löschen einer nicht existierenden ID sollte keine Fehler erzeugen
        service.deleteGameResult(99)

        val res = service.getGameResults()
        assertEquals(1, res.size)
        assertEquals(gameResult, res[0])
    }

    @Test
    fun test_getGameResults_returnsCopy() {
        val gameResult = GameResult(0, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        // Liste holen
        val res = service.getGameResults()

        // erzeugen einer veränderbaren Kopie, diese wird manipuliert
        val modified = res.toMutableList()
        modified.clear()

        // Wenn der Service korrekt eine Kopie zurückgibt, sollte die interne Liste davon nicht betroffen sein.
        val storedResults = service.getGameResults()
        assertEquals(1, storedResults.size)
        assertEquals(gameResult, storedResults[0])
    }
}