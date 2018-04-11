package cl.cariola.tsummary.business.entities

enum class Estados(val value : Int) {
    NUEVO(0),
    EDITADO(1),
    ELIMINADO(2),
    ANTIGUO(3);

    companion object {
        fun from(findValue: Int): Estados = Estados.values().first { it.value == findValue }
    }
}