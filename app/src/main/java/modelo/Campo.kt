package modelo

enum class CampoEvento { ABERTURA, MARCACAO, DESMARCACAO, EXPLOSAO,  RENICIALIZACAO }

data class Campo(val linha: Int, val coluna: Int){
    private val vizinhos = ArrayList<Campo>()
    private val callBacks = ArrayList<(Campo, CampoEvento) -> Unit>()

    var marcado: Boolean = false
    var aberto: Boolean = false
    var minado: Boolean = false

    //somente leitura
    val desmarcado: Boolean get() = !marcado
    val fechado: Boolean get() = !aberto
    val seguro: Boolean get() = !minado

    val objetivoAlcancado: Boolean get() = seguro && aberto || minado && marcado
    val qtdeVizinhosMinados: Int get() = vizinhos.filter{it.minado}.size
    val vizinhancaSegura: Boolean get() = vizinhos.map { it.seguro }
        .reduce{resultado, seguro -> resultado && seguro}

    fun addVizinho(vizinho: Campo){
        vizinhos.add(vizinho)
    }

    fun onEvento(callback: (Campo, CampoEvento) -> Unit){
        callBacks.add(callback)
    }

    // ações executadas ao clicar no campo
    fun abrir(){
        if(fechado){
            aberto = true
            if(minado){
                callBacks.forEach{ it(this, CampoEvento.EXPLOSAO)}
            } else {
                callBacks.forEach { it(this, CampoEvento.ABERTURA) }
                vizinhos.filter { it.fechado && it.seguro && vizinhancaSegura }. forEach { it.abrir() }
            }
        }
    }

    fun alterarMarcacao(){
        if(fechado){
            marcado = !marcado
            val evento = if (marcado) CampoEvento.MARCACAO else CampoEvento.DESMARCACAO
            callBacks.forEach{it(this, evento)}
        }
    }

    fun minar(){
        minado = true
    }

    fun reiniciarCampo(){
        aberto = false
        minado = false
        marcado = false
        callBacks.forEach { it(this, CampoEvento.RENICIALIZACAO) }
    }
}