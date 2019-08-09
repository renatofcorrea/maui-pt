/*
 *    PortugueseStemmer.java
 *    Copyright (C) 2004-2005 Maria Abadia Lacerda Dias
 *    Copyright (C) 2019-2020 Renato Correa
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.entopix.maui.stemmers;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.lucene.LucenePackage;


/**
 * Implements a Portuguese stemmer. The algorithm is based on the paper
 * "A Stemming Algorithm for the Portuguese Language" by Vivianne Orengo
 * and Chris Huyck, in Proceedings of the SPIRE, November, 2001. Several
 * rules where updated and many exceptions and special cases where added.
 *
 * @author  Maria Abadia Lacerda Dias (mald@univates.br)
 * @author Renato Correa (renatocorrea@gmail.com)
 * @version 1.1
 */
public class PortugueseStemmer extends Stemmer implements Serializable {

  // TODO
  // - SRules dão radical diretamente?
  // - modificar tamanho mínimo de stem para tamanho da palavra?
  // - use a a HashMap instead of a String array?

  private static Rule[] plural = {
    // -NS
    new PRule("ns",      1, "m",  new String[] {"íons", "elétrons", "prótons",
    "nêutrons", "fótons", "epsilons"}), // EX: uns
    // -ÃES, -ÕES
    new PRule("ães",     1, "ão", new String[] {"mães"}), // EX: pães
    new PRule("ões",     2, "ão"), // EX: ações
    // -IS
    new PRule("ais",     1, "al", new String[] {"cais", "mais", "pais",
    "demais", "ademais", "jamais", "anais"}), // EX: sais
    new PRule("éis",     1, "el", new String[] {"réis"}), // EX: anéis
    new PRule("eis",     3, "el", new String[] {"fósseis", "táteis", "répteis", 
    "fáceis", "frágeis", "têxteis", "férteis", "inférteis", "voláteis",
    "inúteis", "dóceis", "indóceis", "estéreis", "hábeis", "inábeis",
    "portáteis", "débeis", "mísseis", "fúteis", "vibráteis", "verocímeis",
    "projéteis"}), // EX: móveis
    new PRule("eis",     2, "il"), // EX: úteis
    new PRule("óis",     3, "ol", new String[] {"heróis"}), // EX: anzóis
    new PRule("uis",     2, "ul", new String[] {"caquis", "sanguis",
    "croquis", "sambaquis"}), // EX: azuis
    new SRule("álcoois",    "álcool"),
    new PRule("is",      2, "il", new String[] {"lápis", "cais", "mais", 
    "crúcis", "biquínis", "pois", "depois", "dois", "oásis", "pais", "demais",
    "ademais", "jamais", "anais", "reis", "leis", "práxis", "quis", "tênis",
    "sífilis", "pênis", "bois", "grátis", "oásis", "brócolis", "pélvis",
    "júris", "álcalis", "zumbis", "púbis", "clitóris", "bílis", "bisturis",
    "íris", "táxis", "alíbis", "guris", "chassis", "abacaxis", "caquis",
    "sanguis", "croquis", "sambaquis"}), // EX: anis
    // -ES
    new PRule("les",     2, "l",  new String[] {"simples", "deles", "aqueles", 
    "daqueles", "controles", "àqueles", "neles", "naqueles", "vales", "peles",
    "isósceles", "móbiles", "hipérboles", "sístoles", "metrópoles"}),// EX: males
    new PRule("nes",     4, "n",  new String[] {"perenes", "autóctones",
    "microfones", "cabines", "aborígenes", "telefones", "gramofones"}),// EX: cânones
    new PRule("eses",    4, "ês", new String[] {"*teses", "*gêneses", "dioceses"}),// EX: chineses
    new PRule("ses",     1, "s",  new String[] {"*pses", "*sses", "*fases", 
    "*frases", "*tases", "*oses", "*teses", "*enses", "*gêneses",
    "dioceses", "análises", "bases", "crises"}),// EX: ases
    new PRule("res",     1, "r",  new String[] {"*bres", "*cres", "*dres",
    "*fres", "*gres", "*tres", "*vres", "árvores", "softwares", "hardwares",
    "pires", "escores", "torres", "hectares", "alferes", "alhures", "víveres",
    "títeres", "alqueires", "porres"}), // EX: ares
    new PRule("zes",     2, "z",  new String[] {"fezes", "deslizes", "varizes",
    "bronzes"}),// EX: vezes
    // -S
    new PRule("s",       1, "",   new String[] {"aliás", "pires", "lápis",
    "cais", "mais", "mas", "menos", "férias", "fezes", "pêsames", "crúcis",
    "gás", "atrás", "trás", "detrás", "moisés", "através", "convés", "invés",
    "*ês", "país", "após", "ambas", "ambos", "messias", "oásis", "ônibus",
    "dois", "duas", "três", "depois", "revés", "seis", "dezesseis", "atlas",
    "alvíssaras", "anais", "antolhos", "calendas", "cãs", "condolências",
    "exéquias", "fastos", "núpcias", "mês", "olheiras", "primícias", "víveres",
    "viés", "demais", "ademais", "jamais", "réis", "grátis", "brócolis",
    "pélvis", "púbis", "clitóris", "ânus", "bílis", "íris", "isósceles",
    "simples", "parênteses", "apenas", "vós", "nós", "antes", "pós", "deus",
    "cós", "status", "caos", "ônus", "vírus", "tônus", "bônus", "versus",
    "campus", "stress", "corpus", "través"}), // EX: os
  };

  private static Rule[] adverb = {
    // -MENTE
    new PRule("mente", 4, "", new String[] {"movimente", "*argumente",
    "fragmente", "implemente", "incremente", "decremente", "experimente",
    "complemente", "*regulamente", "instrumente", "cumprimente", "arregimente",
    "fundamente", "suplemente", "sedimente", "parlamente", "documente",
    "*compartimente", "atormente", "*alimente", "ornamente", "regimente",
    "pavimente", "sacramente"}) // EX: premente
  };

  private static Rule[] feminine = {
    // -ORA
    new PRule("dora",      3, "dor"), // EX: amadora
    new PRule("sora",      3, "sor"), // EX: censora
    new PRule("tora",      2, "tor",   new String[] {"fatora"}), // EX: autora
    new SRule("senhora",      "senhor"),
  //new PRule("ana",       3, "ano",   new String[] {"semana", "banana", "membrana", "campana", "guiana", "porcelana", "caravana", "gincana", "bacana", "cabana", "nirvana", "roldana", "savana", "persiana", "ratazana"}), // EX: humana
  //new PRule("ena",       2, "eno",   new String[] {"*ígena", "arena", "antena", "quarentena", "dezena", "centena", "quinzena", "trena", "safena", "novena", "galena", "gangrena", "cadena", "cantilena", "hiena", "açucena",}), // EX:plena
  //new PRule("ina",       2, "ino",   new String[] {"*rotina", "*medicina", "*ficina", "máquina", "resina", "página", "dentina", "lâmina", "china", "marina", "retina", "bobina", "gasolina", "vitamina", "insulina", "piscina", "vagina", "esquina", "turbina", "cartolina", "cortina", "vacina", "parafina", "gelatina", "campina", "cantina", "margarina", "neblina", "faxina", "colina", "batina", "ravina", "rapina", "lamparina", "surdina", "propina", "toxina", "marina", "latrina", "jogatina", "tangerina", "botina", "maestrina"}), // EX: latina
    // -ONA
    new PRule("ona",       3, "ão",    new String[] {"*crona", "*erona",
    "*iona", "*lona", "*nona", "desabona", "abandona", "telefona", "sanfona",
    "antígona", "mamona", "japona", "corona", "poltrona", "matrona",
    "manjerona", "dipirona", "destrona", "persona", "unísona", "*cortisona",
    "*metasona", "monótona", "maratona", "*cetona", "detona", "*oxítona",
    "apaixona", "azeitona", "*zona"}), // EX: bobona
  //new PRule("rna",       2, "rno",   new String[] {"perna", "caverna", "lanterna", "caserna", "sarna", "baderna", "bigorna", "taberna", "cisterna", "taberna", "furna"}),  // EX: terna
  //new PRule("gna",       2, "gno"), // EX: digna
    // -ESA
    new SRule("baronesa",    "barão"),
    new SRule("duquesa",     "duque"),
    new SRule("princesa",    "príncipe"),
    new PRule("esa",      4, "ês",    new String[] {"*presa", "*defesa",
    "despesa", "sobremesa", "turquesa"}), // EX: chinesa
  //new PRule("osa",      2, "oso",   new String[] {"mucosa", "prosa", "glosa"}), //EX: idosa
  //new PRule("usa",      2, "uso",   new String[] {"lousa", "cousa", "blusa", "deusa", "medusa", "hipotenusa", "menopausa"}),       // EX: difusa
  //new PRule("nsa",      2, "nso",   new String[] {"imprensa", "ofensa", "despensa"}), //EX: densa
  //new PRule("rsa",      2, "rso",   new String[] {"conversa", "farsa", "persa", "morsa"}), //EX: versa
  //new PRule("ossa",     1, "osso",  new String[] {"bossa", "crossa"}), //EX: nossa
  //new PRule("íaca",     3, "íaco"), //EX: maníaca
  //new PRule("ica",      2, "ico",   new String[] {"*réplica", "república", "fábrica", "cólica", "súplica", "encíclica", "rubrica"}), // EX: única
  //new PRule("ida",      1, "ido",   new String[] {"*icida", "*amida", "vida", "dúvida", "dívida", "sobrevida", "avenida", "margarida", "grávida", "carótida", "jazida", "revida", "guarida", }), // EX: lida
  //new PRule("ída",      2, "ído"), // EX: saída
  //new PRule("oda",      1, "odo" ,  new String[] {"soda", "abóboda"}), // EX: toda
  //new PRule("uda",      1, "udo" ,  new String[] {"arruda", "cauda", "bermuda"}), // EX: muda
  //new PRule("lda",      2, "ldo" ,  new String[] {"esmeralda", "grinalda", "fralda"}), // EX: molda
  //new PRule("nda",      1, "ndo",   new String[] {"ainda", "onda", "fenda", "bunda", "umbanda", "microonda", "reprimenda", "berlinda", "microfenda", "abunda", "panda", "guirlanda", "barafunda", "microsonda"}), // EX: banda
  //new PRule("rda",      2, "rdo",   new String[] {"perda", "corda", "vanguarda", "retaguarda", "salvaguarda", "espingarda", "merda", "mostarda", "jarda"}), // EX: guarda
    // -Ã
    new PRule("ã",        2, "ão",    new String[] {"manhã", "amanhã", "maçã",
    "xamã", "afã", "clã", "*ímã", "divã", "sutiã", "titã", "tucumã", "marzipã"}), // EX: grã
    // -ÉSIMA
    new PRule("ésima",    2, "ésimo"), // EX: enésima
    // -ÍSSIMA, -ÉRRIMA, -IVA, -EIRA
    new PRule("íssima",   3, "íssimo"), // EX: belíssima
    new PRule("érrima",   3, "érrimo"), // EX: altérrima
    new PRule("iva",      4, "ivo",   new String[] {"gengiva"}), // EX: efetiva
    new PRule("eira",     3, "eiro",  new String[] {"madeira", "cadeira", "ribeira", "bandeira", "esteira", "peneira", "ladeira", "derradeira", "requeira", "caveira", "lareira"}), // EX: ligeira
    // -IZADA, -ADA, -ENTA
    new PRule("izada",    4, "izado"), // EX: realizada
    new PRule("ada",      1, "ado",   new String[] {"*camada", "pitada", "entrada", "década", "cada", "nada", "jornada", "palmada", "batelada", "enxada", "congada", "espada", "risada", "saraivada", "tonelada", "marmelada", "goiabada", "lombada", "camarada", "trovoada", "chuvarada", "toada", "ossada", "macacada", "granada", "gônada", "alvorada", "chibatada", "salada", "peixada", "lâmpada", "meninada", "molecada", "mulherada", "criançada", "olimpíada", "colherada", "facada", "panelada", "cilindrada", "escada", "cabeçada", "cachorrada", "joelhada", "bofetada", "barrigada", "narigada", "manada", "lambada", "jangada", "porrada", "dentada", "cilada", "arcada", "moçada", "polegada", "garotada", "papelada", "pomada", "piazada", "balada", "almofada", "rapaziada", "feijoada", "ninhada", "bolada", "boiada"}), // EX: dada
    new PRule("enta",     3, "ento",  new String[] {"quarenta", "cinqüenta", "sessenta", "setenta", "oitenta", "noventa", "pimenta", "placenta", "tormenta", "parenta", "polenta", "magenta"}), // EX: aumenta
    // -OA
    new NRule("oa",          "ão",    new String[] {"leitoa", "patroa", "leoa"}),
    // -ISA
    new NRule("tisa",        "ta",    new String[] {"poetisa", "profetisa"}),
    new SRule("sacerdotisa", "sacerdote"),
    // -TRIZ
    new NRule("triz",        "tor",   new String[] {"atriz", "imperatriz"}),
    // ...
    new SRule("sílfide",     "silfo"),
    new SRule("diaconisa",   "diácono"),
    new SRule("égua",        "cavalo"),
    new SRule("galinha",     "galo"),
    new SRule("maestrina",   "maestro"),
    new SRule("monja",       "monge"),
    new SRule("rainha",      "rei"),
  };

  private static Rule[] augmentative = {
    // -ZONA
    new PRule("zona",           3, "",   new String[] {"macrozona", "microzona", "subzona", "biozona", "*butazona"}), // EX: mãezona
    // -ÍSSIMO
    new PRule("bilíssimo",      3, "vel"), // EX: amabilíssimo
    new SRule("antiquíssimo",      "antigo"),
    new PRule("quíssimo",       2, "co"), // EX: pouquíssimo
    new PRule("díssimo",        3, "do", new String[] {"grandíssimo"}), // EX: lindíssimo
    new SRule("amicíssimo",        "amigo"),
    new SRule("dulcíssimo",        "doce"),
    new PRule("císsimo",        4, "z"), // EX: ferocíssimo
    new SRule("grandessíssimo",    "grande"),
    new SRule("longuíssimo",       "longo"),
    new PRule("íssimo",         3), // EX: belíssimo
    // -ÉRRIMO
    new SRule("magérrimo",         "magro"),
    new SRule("paupérrimo",        "pobre"),
    new PRule("érrimo",         3), // EX: altérrimo
    // ...
    new PRule("alhão",          3, "",   new String[] {"batalhão", "trabalhão", "trapalhão", "medalhão"}), // EX: vagalhão
    new PRule("aça",            3, "",   new String[] {"ameaça", "abraça", "*faça", "cabaça", "*laça", "espaça", "embaça", "desgraça", "carcaça", "cachaça", "carapaça", "rechaça", "trapaça", "*embaraça", "despedaça", "estilhaça", "arruaça", "*mordaça", "linhaça", "esvoaça", "congraça", "arregaça"}), // EX: ricaça
    new PRule("aço",            3, "",   new String[] {"*espaço", "pedaço", "abraço", "almaço", "*braço", "palhaço", "*embaraço", "cangaço", "rechaço", "retraço", "*faço", "mormaço"}), // EX: golaço
    new PRule("uça",            4), // EX: dentuça
    new PRule("ázio",           3, "",   new String[] {"topázio"}), // EX: copázio
    new PRule("arraz",          4), // EX: pratarraz
    new PRule("arra",           3, "",   new String[] {"esbarra", "cigarra", "bizarra", "fanfarra", "guitarra"}), // EX: bocarra
    new PRule("orra",           3), // EX: patorra
    new PRule("anzil",          4), // EX: corpanzil
    new PRule("aréu",           3), // EX: fogaréu
    new PRule("astro",          4), // EX: poetastro
    new PRule("asta",           4, "",   new String[] {"contrasta", "desgasta", "desbasta"}), // EX: cineasta
    new PRule("asto",           4, "",   new String[] {"*plasto", "*blasto"}), // EX: padrasto
  //new PRule("adão",           3, "ad"), // EX: caladão APENAS RETIRA ÃO
  //new PRule("edão",           1, "ed"), // EX: dedão APENAS RETIRA ÃO
    // -ÃO
    new PRule("zarrão",         3),  // EX: homenzarrão
    new PRule("rrão",           4, "",   new String[] {"empurrão", "macarrão", "chimarrão"}),  // EX: beberrão
    new PRule("zão",            2, "",   new String[] {"*razão", "vazão", "prizão", "coalizão"}),  // EX: pezão
    new SRule("casarão",           "casa"),
    new SRule("asneirão",          "asno"),
    new SRule("toleirão",          "tolo"),
    new SRule("vozeirão",          "voz"),
    new SRule("narigão",           "nariz"),
    new PRule("ão",             3, "", new String[] {"camarão", "chimarrão", "canção", "coração", "embrião", "grotão", "glutão", "ficção", "fogão", "feição", "furacão", "gamão", "lampião", "*leão", "macacão", "nação", "órfão", "orgão", "patrão", "portão", "quinhão", "rincão", "tração", "falcão", "espião", "mamão", "folião", "cordão", "aptidão", "campeão", "colchão", "limão", "leilão", "melão", "barão", "milhão", "bilhão", "fusão", "cristão", "ilusão", "estação", "senão"}), //perdão, feijão, macarrão?
    // ...
    new SRule("fornalha",          "forno"),
    new SRule("gentalha",          "gente"),
    new SRule("muralha",           "muro"),
    new SRule("queixada",          "queixo"),
  };

  private static Rule[] diminutive = {
    // -INHA, -INHO
    new PRule("quinha",     2, "ca", new String[] {"mesquinha"}), // EX: vaquinha
    new PRule("quinho",     2, "co", new String[] {"mesquinho", "parquinho", "molequinho", "bosquinho", "chequinho"}), // EX: saquinho
    new PRule("guinha",     2, "ga", new String[] {"linguinha"}), // EX: ruguinha
    new PRule("guinho",     2, "go", new String[] {"sanguinho"}), // EX: joguinho
    new PRule("zinha",      2, "",   new String[] {"*vizinha", "cozinha", "vozinha", "luzinha", "belezinha", "brazinha"}), // EX: pazinha
    new PRule("zinho",      2, "",   new String[] {"*vizinho", "cozinho", "rapazinho", "quinzinho", "comezinho", "gizinho"}), // EX: pozinho
    new PRule("cinha",      2, "ça", new String[] {"docinha"}), // EX: mocinha
    new PRule("cinho",      2, "ço", new String[] {"focinho", "toicinho", "toucinho", "docinho", "ancinho"}), // EX: lacinho
    new SRule("asinha",        "asa"),
    new SRule("feinha",        "feia"),
    new SRule("joinha",        "jóia"),
    new SRule("meinha",        "meia"),
    new SRule("sainha",        "saia"),
    new SRule("veinha",        "veia"),
    new PRule("inha",       3, "a",  new String[] {"*caminha", "mantinha", "continha", "farinha", "marinha", "espinha", "detinha", "*linha", "sobrinha", "obtinha", "convinha", "advinha", "campainha", "ladainha", "engatinha", "intervinha", "andorinha", "mesquinha", "*vizinha", "cozinha"}), // EX: bolinha
    new SRule("aninho",        "ano"),
    new SRule("radinho",       "radio"),
    new PRule("inho",       3, "o",  new String[] {"caminho", "carinho", "sobrinho", "marinho", "cadinho", "espinho", "redemoinho", "advinho", "engatinho", "pergaminho", "encaminho", "torvelinho", "cominho", "golfinho", "mesquinho", "*vizinho", "cozinho", "comezinho", "focinho", "toicinho", "toucinho", "ancinho"}), // EX: bobinho
    // -NINA, -NINO
    new SRule("pequenina",     "pequena"),
    new SRule("pequenino",     "pequeno"),
    // -IM
    new SRule("boletim",       "boleto"),
    new SRule("botequim",      "bar"),
    new SRule("camarim",       "camara"),
    new SRule("espadim",       "espada"),
    new SRule("festim",        "festa"),
    new SRule("folhetim",      "folha"),
    new SRule("fortim",        "forte"),
    // -ELHA, -ELHO
    new SRule("fedelha",       "feder"),
    new SRule("fedelho",       "feder"),
    new SRule("grupelho",      "grupo"),
    new SRule("rapazelho",     "rapaz"),
    // -EJO
    new SRule("animalejo",     "animal"),
    new SRule("festejo",       "festa"),
    new SRule("gracejo",       "graça"),
    new SRule("lugarejo",      "lugar"),
    new SRule("quitalejo",     "quintal"),
    new SRule("sertaneja",     "sertão"),
    new SRule("sertanejo",     "sertão"),
    new SRule("vilarejo",      "vila"),
    // -ILHA, -ILHO
    new SRule("vasilha",       "vaso"),
    new PRule("ilha",       4, "",   new String[] {"compartilha", "maravilha", "desempilha", "desvencilha", "fervilha", "engatilha", "lentilha"}), // EX: planilha
    new PRule("ilho",       4, "",   new String[] {"compartilho", "maravilho", "desempilho", "desvencilho", "fervilho", "engatilho"}), // EX: polvilho
    // -ACHO
    new SRule("fogacho",       "fogo"),
    new SRule("penacho",       "pena"),
    new SRule("populaçho",     "povo"),
    new SRule("riacho",        "rio"),
    // -ICHA, -ICHO
    new SRule("barbicha",      "barba"),
    new SRule("governicho",    "governo"),
    // -UCHA, -UCHO
    new SRule("capucha",       "capa"),
    new SRule("casucha",       "casa"),
    new SRule("capucho",       "capa"),
    new SRule("cartucho",      "carta"),
    new SRule("gorducho",      "gordo"),
    new SRule("papelucho",     "papel"),
    new SRule("pequerrucho",   "pequeno"),
    // -EBRE
    new SRule("casebre",       "casa"),
    // -ECA, -ECO
    new SRule("boteco",        "bar"),
    new SRule("filmeco",       "filme"),
    new SRule("soneca",        "sono"),
    new SRule("folheca",       "folha"),
    new SRule("jornaleco",     "jornal"),
    new SRule("livreco",       "livro"),
    // -ICO
    new SRule("burrico",       "burro"),
    new SRule("amorico",       "amor"),
    new SRule("namorico",      "namoro"),
    // -ELA
    new SRule("ruela",         "rua"),
    new SRule("viela",         "via"),
    new SRule("cidadela",      "cidade"),
    new SRule("mordidela",     "morder"),
    new SRule("olhadela",      "olho"),
    new SRule("piscadela",     "piscar"),
    new SRule("sacudidela",    "sacudir"),
    // -ETE
    new PRule("onete",      3), // EX: sabonete
    new PRule("uete",       3), // EX: foguete
    new NRule("ete",           "",   new String[] {"balancete", "barrilete", "bracelete", "cacetete", "canivete", "capacete", "cartazete", "cavalete", "claquete", "clarinete", "corpete", "estilete", "florete", "lembrete", "martelete", "palacete", "patinete", "ramalhete", "rolete", "tablete", "trompete", "verbete", "artiguete", "malandrete"}),
    new SRule("disquete",      "disco"),
    new SRule("gabinete",      "cabine"),
    new SRule("charrete",      "carro"),
    // -ETA
    new PRule("oneta",      3), // EX: motoneta
    new NRule("queta",         "c",  new String[] {"barqueta", "fabriqueta", "plaqueta"}),
    new NRule("eta",           "",   new String[] {"bicicleta", "camiseta", "faceta", "motocicleta", "caderneta", "caixeta", "saleta", "carreta", "chupeta", "vareta", "prancheta", "mureta", "maleta", "corneta", "clarineta", "chaveta", "barqueta", "saleta", "papeleta", "maneta", "fabriqueta", "canaleta", "trombeta", "estatueta", "marreta", "historieta", "filipeta", "costeleta"}),
    new SRule("tabuleta",      "tábua"),
    // -ETO
    new NRule("eto",           "",   new String[] {"livreto", "carreto", "poemeto", "verseto", "esboceto"}),
    new SRule("libreto",       "livro"),
    // -ZITA, -ZITO
    new SRule("florzita",      "flor"), // EX: florzita
    new SRule("jardinzito",    "jardim"), // EX: jardinzito
    // -ITA, -ITO
    new SRule("pequetita",     "pequena"),
    new NRule("ita",           "",   new String[] {"blusita", "camisita", "carmelita", "fulanita", "israelita", "salita", "saudita", "senhorita"}),
    new SRule("mosquito",      "mosca"),
    new SRule("palito",        "pau"),
    new SRule("pequetito",     "pequeno"),
    new NRule("ito",           "",   new String[] {"cabrito", "modelito", "negrito", "erudito", "rapazito"}),
    // -OTA, -OTE
    new SRule("velhota",       "velha"),
    new NRule("ote",           "",   new String[] {"filhote", "meninote", "grandote", "pequenote", "molecote", "fracote", "serrote", "velhote", "sacerdote", "cabeçote", "caixote", "malote", "camarote"}),
    // -ISCA, -ISCO
    new NRule("isca",          "",   new String[] {"mourisca", "talisca"}),
    new NRule("isco",          "",   new String[] {"mourisco", "chuvisco", "levantisco", "pedrisco"}),
    // -USCA, -USCO
    new NRule("usca",          "",   new String[] {"velhusca"}),
    new NRule("usco",          "",   new String[] {"velhusco", "chamusco"}),
    // -OLA
    new NRule("ola",           "",   new String[] {"fazendola", "rapazola", "marola", "bandeirola", "camisola", "gabarola", "mariola", "casinhola", "ventarola", "pianola", "cachola"}),
    // -ULA, -ULO
    new NRule("úncula",        "",   new String[] {"questiúncula"}),
    new NRule("únculo",        "",   new String[] {"homúnculo", "pedúnculo"}),
    new NRule("úscula",        "",   new String[] {"maiúscula", "minúscula"}),
    new NRule("úsculo",        "",   new String[] {"corpúsculo", "opúsculo", "maiúsculo", "minúsculo"}),
    new NRule("áculo",         "",   new String[] {"vernáculo", "sustentáculo", "receptáculo", "habitáculo", "tabernáculo", "tentáculo"}),
    new NRule("érculo",        "",   new String[] {"tubérculo"}),
    new NRule("ícula",         "",   new String[] {"febrícula", "gotícula", "partícula", "película", "radícula", "matrícula", "quadrícula", "vesícula"}),
    new NRule("ículo",         "",   new String[] {"montículo", "vermículo", "versículo", "ventrículo", "folículo", "fascículo", "testículo", "cubículo", "funículo", "pedículo", "ossículo", "montículo", "canalículo"}),
    new NRule("ula",           "",   new String[] {"nótula", "rótula", "molécula"}),
    new NRule("ulo",           "",   new String[] {"glóbulo", "grânulo",  "módulo", "nódulo", "régulo"}),
  };

  private static Rule[] numeral = {
    new PRule("ésimo", 2), // EX: enésimo
  };

  /*    new SRule("",    ""),
        new PRule("", ?, "", new String[] {"", ""}), // EX:
        new NRule("",    "", new String[] {"", ""}),
  */

  private static Rule[] noun = {
    // -ADO
    new PRule("bilizado",   2, "v"), // EX: mobilizado
    new PRule("alizado",    4), // EX: globalizado
    new PRule("atizado",    4), // EX: privatizado
    new PRule("tizado",     4), // EX: enfatizado
    new PRule("izado",      4), // EX: elitizado
    new PRule("ado",        2, "",   new String[] {"prado", "grado", "veado", "brado", "alado", "estado", "senado", "sábado", "fígado", "bêbado", "bocado", "rosado", "enfado", "côvado", "mercado", "machado", "quadrado", "deputado", "telhado", "eldorado", "feriado"}),
    // -ENTO
    new PRule("guento",     4, "g"), // EX: briguento
    new PRule("quento",     4, "c"), // EX: melequento
    new NRule("ulento",        "",   new String[] {"corpulento", "turbulento", "fraudulento", "truculento", "purulento", "virulento"}),
    new SRule("sangrento",     "sangue"),
    new SRule("sonolento",     "sono"),
    new PRule("ento",       3, "",   new String[] {"*mento", "*vento", "*tento", "*sento", "enfrento", "talento", "acrescento", "sargento", "desalento", "relento", "rebento", "arrebento"}), // EX: nojento
    // -IVO
    new PRule("ativo",      4, ""), // EX: educativo
    new PRule("tivo",       4, ""), // EX: objetivo
    new SRule("defensivo",     "defender"),
    new PRule("ivo",        4, "",   new String[] {"arquivo", "passivo", "massivo", "ostensivo", "defensivo", "remissivo", "convivo", "cursivo", "compassivo", "lascivo", "elusivo"}), // EX: adesivo
    // -ISTA
    new PRule("encialista", 5), // EX: existencialista
    new PRule("alista",     5), // EX: capitalista
    new PRule("icionista",  4), // EX: nutricionista
    new PRule("cionista",   4), // EX: reducionista
    new PRule("ionista",    4), // EX: pensionista
    new NRule("tista",         "t",  new String[] {"cientista", "renascentista", "separatista", "corporatista", "preventista"}),//aterado de c para t por causa de ciencia e cientista
    new SRule("estatista",     "estado"),
    new PRule("ista",       3), // EX: artista
    // -AGEM
    new PRule("izagem",     6), // EX: aprendizagem
    new PRule("agem",       3, "",   new String[] {"mensagem", "*vantagem", "paisagem", "interagem", "homenagem", "bagagem", "*imagem", "massagem", "garagem", "chantagem", "estalagem", "fuselagem", "carenagem"}), // EX: passagem
    new NRule("agem",          "ag", new String[] {"mensagem", "*vantagem", "paisagem", "interagem", "homenagem", "bagagem", "*imagem", "massagem", "garagem", "chantagem", "estalagem", "fuselagem", "carenagem"}),
    // -MENTO
    new PRule("amento",     3, "",   new String[] {"filamento", "firmamento", "departamento"}), // EX: tratamento
    new PRule("imento",     3, "",   new String[] {"detrimento", "pavimento", "condimento"}), // EX: movimento
    // -IDO, -ÍDO
    new PRule("ido",        3, "",   new String[] {"líquido", "marido", "fluido", "*válido", "sólido", "híbrido", "rígido", "*óxido", "libido", "nítido", "tímido", "bandido", "comprido", "valido", "*ácido", "pálido", "liquido", "*lúcido", "límpido", "prurido", "mórbido", "estúpido", "convido", "decido", "plácido", "esplêndido", "sórdido", "grávido", "insípido", "explêndido", "cálido", "vívido", "sustenido", "pútrido", "lívido", "lânguido", "fúlgido", "flácido", "anidrido", "tórrido", "*árido", "lépido", "intrépido", "impávido", "esquálido", "tépido", "pérfido", "frígido", "bólido", "cândido", "duvido"}),
    new PRule("ído",        3, "",   new String[] {"*aldeído"}),

    /*---------------- não revisado ----------------*/
    // -OR
    new PRule("ador",       3), // EX: gerador
    new PRule("edor",       3), // EX: devedor
    new PRule("idor",       3), // EX: medidor
    new PRule("dor",        2, "d",  new String[] {"condor"}), // EX: pudor
    new PRule("ssor",       3, "ss"), //EX: emissor
    new PRule("sor",        4, "s"), //EX: divisor
    new PRule("tor",        3, "",   new String[] {"leitor", "doutor", "escritor", "monitor", "reitor", "pastor", "gestor", "agricultor", "benfeitor", "consultor"}), // EX: reator **parcialmente revisto**
    new PRule("or",         2, "",   new String[] {"maior", "menor", "melhor", "redor", "rigor", "tambor", "tumor", "pastor", "interior", "favor", "autor"}), // EX: valor
    // -ESCO
    new NRule("esco",          "",   new String[] {"burlesco", "principesco", "parentesco", "gigantesco", "romanesco"}),
    new PRule("esco",       4),

    new PRule("atória",     5),
    new PRule("oria",       4, "",   new String[] {"categoria"}),
    //new PRule(aria, gritaria, pirataria),
    //new PRule(ia, advocacia, reitoria),
    //new PRule(ia, alegria, valentia),

    new PRule("ário",       3, "",   new String[] {"voluntário", "salário", "aniversário", "*lionário", "armário"}), // diário?
    new PRule("atório",     3),
    new PRule("rio",        5, "",   new String[] {"voluntário", "aniversário", "compulsório", "*lionário", "*stério"}), // diário, salário, próprio, armário?
    new PRule("ério",       6),

    new PRule("abilidade",  5),
    new PRule("ividade",    5),
    new PRule("idade",      4, "",   new String[] {"autoridade", "comunidade"}),
    //new PRule(dade, dignidade, crueldade),

    new PRule("ionar",      5),

    new PRule("ional",      4),

    new PRule("ência",      3),
    new PRule("ância",      4, "",   new String[] {"ambulância"}),

    new PRule("edouro",     3),

    new PRule("queiro",     3, "c"),
    new PRule("adeiro",     4, "",   new String[] {"desfiladeiro", "verdadeiro"}),
    new PRule("eiro",       3, "",   new String[] {"desfiladeiro", "pioneiro", "mosteiro"}),

    new PRule("uoso",       3),
    new PRule("oso",        3, "",   new String[] {"precioso"}),

    new PRule("alizaç",     5),
    new PRule("atizaç",     5),
    new PRule("tizaç",      5),
    new PRule("izaç",       5, "",   new String[] {"organizaç"}),
    new PRule("aç",         3, "",   new String[] {"equaç", "relaç"}), // -AÇÃO
    new PRule("iç",         3, "",   new String[] {"eleiç"}),

    new PRule("ês",         4),
    new PRule("eza",        3),
    new PRule("ez",         4),

    new PRule("ante",       2, "",   new String[] {"gigante", "elefante", "adiante", "possante", "instante", "restaurante"}),

    new PRule("ástico",     4, "",   new String[] {"eclesiástico"}),
    new PRule("alístico",   3),
    new PRule("áutico",     4),
    new PRule("êutico",     4),
    new PRule("ático",      4, "",   new String[] {"alopático"}), // problemático, emblemático
    new PRule("tico",       3, "",   new String[] {"político", "eclesiástico", "diagnóstico", "prático", "doméstico", "diagnóstico", "idêntico", "alopático", "artístico", "autêntico", "eclético", "crítico", "critico"}),
    // ídico, dico
    new PRule("ico",        4, "",   new String[] {"*tico", "público", "explico"}),

    new PRule("encial",     5),

    new PRule("auta",       5),

    new PRule("quice",      4, "c"),
    new PRule("ice",        4, "",   new String[] {"cúmplice"}),

    new PRule("íaco",       3),

    new PRule("ente",       4, "",   new String[] {"freqüente", "alimente", "acrescente", "permanente", "aparente"}), // oriente?
    new PRule("ense",       5),

    new PRule("inal",       3),

    new PRule("ano",        4),

    new PRule("ável",       2, "",   new String[] {"afável", "razoável", "potável", "vulnerável"}),
    new PRule("ível",       3, "",   new String[] {"possível"}),
    new PRule("vel",        5, "",   new String[] {"possível", "vulnerável"}), // solúvel?

    new PRule("bil",        3, "vel"), // *vel?

    new PRule("ura",        4, "",   new String[] {"imatura", "acupuntura", "costura"}),

    new PRule("ural",       4),
    new PRule("ual",        3, "",   new String[] {"bissexual", "virtual", "visual", "pontual"}),
    new PRule("ial",        3),
    new PRule("al",         4, "",   new String[] {"afinal", "animal", "estatal", "bissexual", "desleal", "fiscal", "formal", "pessoal", "liberal", "postal", "virtual", "visual", "pontual", "sideral", "sucursal"}),

    new PRule("alismo",     4),
    new PRule("ivismo",     4),
    new PRule("ismo",       3, "",   new String[] {"cinismo"}),

    //Esses são sufixos que formam substantivos de outros substantivos...Ler 1ªObservação, pg.96
    //new PRule(ato, carbonato, sulfato),
    //new PRule(alha, canalha, gentalha),
    //new PRule(ama, dinheirama),
    //new PRule(ame, vasilhame),
    //new PRule(edo, olivedo, vinhedo),
    //new PRule(eira, copeira, poeira),
    //new PRule(io, gentio, mulherio),
    //new PRule(ite, bronquite, gastrite),
    //new PRule(ugem, ferrugem, penugem),
    //new PRule(ume, cardume, negrume),

    //Esses são sufixos que formam substantivos de adjetivos...Ler 2ª Observação, pg.96
    //new PRule(i(dão), gratidão, mansidão),
    //new PRule(ície, calvície, imundície),
    //new PRule(i(tude), altitude, magnitude),

    new PRule("quid",       3, "co"), // EX: rouquidão
    new PRule("id",         3, "",   new String[] {"solid", "multid", "partid", "marid", "partid"}), // EX: aptidão

    //Esses são sufixos que formam substantivos de verbos...Ler Observação, pg.98
    //new PRule(ança, lembrança, vingança),
    //new PRule(ença, descrença, diferença),
    //new PRule(inte, ouvinte, pedinte),
    //new PRule(ção, nomeação, traição),
    //new PRule(são, agressão, extensão),
    //new PRule(douro, bebedouro, suadouro),
    //new PRule(tório, lavatório, vomitório),
    //new PRule(dura, atadura),
    //new PRule(tura, pintura, magistratura),
    //new PRule(sura, clausura, tonsura),

    //Esses são sufixos que formam adjetivos de substantivos...Ler Observação, pg.99

    //new PRule(aco, maníaco, austríaco),
    //new PRule(aico, judaico, prosaico),
    //new PRule(ar, escolar, familiar),
    //new PRule(ão, alemão, beirão),
    //new PRule(engo, mulherengo),
    //new PRule(enho, ferrenho),
    //new PRule(eno, terreno),
    //new PRule(eo, róseo, férreo),
    //new PRule(este, agreste, celeste),
    //new PRule(estre, campestre, terrestre),
    //new PRule(eu, europeu, hebreu),
    //new PRule(ício, alimentício, natalício),
    //new PRule(il, febril, senhoril),
    //new PRule(ino, londrino, cristalino), É também diminutivo
    //new PRule(ita, israelita, islamita),
    //new PRule(onho, enfadonho),
    //new PRule(udo, pontudo, barbudo),

    //Esses são sufixos que formam adjetivos de verbos...Ler Observação, pg.100

    //new PRule(inte, constituinte, seguinte),
    //new PRule(io, tardio),
    //new PRule(d(iço), movediço, quebradiço),
    //new PRule(t(ício), acomodatício, factício),

    //Esses são sufixos verbais...pg.101

    //new PRule(ear, cabecear, folhear),
    //new PRule(ejar, gotejar, velejar),
    //new PRule(entar, amolentar),
    //new PRule(i(ficar), clarificar, dignificar),
    //new PRule(icar, bebericar, depenicar),
    //new PRule(ilhar, dedilhar, fervilhar),
    //new PRule(inhar, escrevinhar, cuspinhar),
    //new PRule(iscar, chuviscar, lambiscar),
    //new PRule(itar, dormitar, saltiar),
    //new PRule(izar, civilizar, utilizar),
  };

  private static PRule[] verb = {
    new PRule("aríamo", 2),
    new PRule("ássemo", 2),
    new PRule("eríamo", 2),
    new PRule("êssemo", 2),
    new PRule("iríamo", 3),
    new PRule("íssemo", 3),
    new PRule("áramo",  2),
    new PRule("árei",   2),
    new PRule("aremo",  2),
    new PRule("ariam",  2),
    new PRule("aríei",  2),
    new PRule("ássei",  2),
    new PRule("assem",  2),
    new PRule("ávamo",  2),
    new PRule("êramo",  3),
    new PRule("eremo",  3),
    new PRule("eriam",  3),
    new PRule("eríei",  3),
    new PRule("êssei",  3),
    new PRule("essem",  3),
    new PRule("íramo",  3),
    new PRule("iremo",  3),
    new PRule("iriam",  3),
    new PRule("iríei",  3),
    new PRule("íssei",  3),
    new PRule("issem",  3),
    new PRule("ando",   2),
    new PRule("endo",   3),
    new PRule("indo",   3),
    new PRule("ondo",   3),
    new PRule("aram",   2),
    new PRule("arão",   2),
    new PRule("arde",   2),
    new PRule("arei",   2),
    new PRule("arem",   2),
    new PRule("aria",   2),
    new PRule("armo",   2),
    new PRule("asse",   2),
    new PRule("aste",   2),
    new PRule("avam",   2, "", new String[] {"agravam"}),
    new PRule("ávei",   2),
    new PRule("eram",   3),
    new PRule("erão",   3),
    new PRule("erde",   3),
    new PRule("erei",   3),
    new PRule("êrei",   3),
    new PRule("erem",   3),
    new PRule("eria",   3),
    new PRule("ermo",   3),
    new PRule("esse",   3),
    new PRule("este",   3, "", new String[] {"faroeste", "agreste"}),// noroeste, sudeste, sudoeste ??
    new PRule("íamo",   3),
    new PRule("iram",   3),
    new PRule("íram",   3),//caíram, saíram...Eu acho que tem que tirar o íram e colocar o ir
    new PRule("irão",   2),
    new PRule("irde",   2),
    new PRule("irei",   3, "", new String[] {"admirei"}),//írei(com acento agudo no i?
    new PRule("irem",   3, "", new String[] {"adquirem"}),
    new PRule("iria",   3),
    new PRule("irmo",   3),
    new PRule("isse",   3),
    new PRule("iste",   4),
    new PRule("iava",   4, "", new String[] {"ampliava"}), // ??
    new PRule("amo",    2),
    new PRule("iona",   3),// ??
    new PRule("ara",    2, "", new String[] {"arara", "prepara"}),
    new PRule("ará",    2, "", new String[] {"alvará"}),
    new PRule("are",    2, "", new String[] {"prepare"}),
    new PRule("ava",    2, "", new String[] {"agrava"}),
    new PRule("emo",    2),
    new PRule("era",    3, "", new String[] {"acelera", "espera"}),
    new PRule("erá",    3),
    new PRule("ere",    3, "", new String[] {"espere"}),// caberes do verbo caber
    new PRule("iam",    3, "", new String[] {"enfiam", "ampliam", "elogiam", "ensaiam"}),
    new PRule("íei",    3),
    new PRule("imo",    3, "", new String[] {"reprimo", "intimo", "íntimo", "*nimo", "queimo", "*ximo"}),
    new PRule("ira",    3, "", new String[] {"fronteira", "sátira"}),
    new PRule("ído",    3),// ??
    new PRule("irá",    3),
    new PRule("tizar",  4, "", new String[] {"alfabetizar"}),// ??
    new PRule("izar",   5, "", new String[] {"organizar"}),// ??
    new PRule("itar",   5, "", new String[] {"acreditar", "explicitar", "estreitar"}),// ??
    new PRule("ire",    3, "", new String[] {"adquire"}),
    new PRule("omo",    3),
    new PRule("ai",     2),
    new PRule("am",     2),
    new PRule("ear",    4, "", new String[] {"alardear", "nuclear"}),
    new PRule("ar",     2, "", new String[] {"azar", "bazar", "patamar"}),
    new PRule("uei",    3),
    new PRule("uía",    5, "u"),
    new PRule("ei",     3),
    new PRule("guem",   3, "g"),
    new PRule("em",     2, "", new String[] {"*alem", "virgem"}),
    new PRule("er",     2, "", new String[] {"éter", "pier"}),
    new PRule("eu",     3, "", new String[] {"chapeu"}), // chapéu?
    new PRule("ia",     3, "", new String[] {"estória", "fatia", "*acia", "praia", "elogia", "mania", "lábia", "aprecia", "polícia", "arredia", "cheia", "*ásia"}),
    new PRule("ir",     3, "", new String[] {"freir"}),
    new PRule("iu",     3),
    new PRule("eou",    5),
    new PRule("ou",     3),
    new PRule("i",      3) //cai do verbo cair, tem que tirar o i e colocar o ir. Tem que colocar todas as terminações das
    //conjugações do verbo cair

   //new PRule(ado, cantado),
   //new PRule(ido, vendido, partido),
  };

  private static PRule[] thematic_vowel = { //Tem que revisar!!!
    new PRule("bil", 2, "vel"), // ????
    new PRule("gue", 2, "g", new String[] {"gangue", "jegue"}),
    new PRule("á",   3),
    new PRule("ê",   3, "",  new String[] {"bebê"}),
    new PRule("a",   3, "",  new String[] {"ásia"}),
    new PRule("e",   3),
    new PRule("o",   3, "",  new String[] {"*ão"})
    // new PRule("spaço",         1, "spac"),  // O QUE FAZER COM O ÇEDILHA???
    // o que fazer com o ÃO?
    // o que fazer com o ÃZ?
  };

  /**
   * Apply the given rule set to word
   */
  private boolean applyRules(StringBuffer word, Rule[] rules)
  {
    boolean changed = false;
    for (int i = 0; i < rules.length; i++) {
      changed = rules[i].apply(word);
      if (changed) {
        // do not apply remaining rules
        break;
      }
    }
    return changed;
  }

  /**
   * Replace accented characters for normal ones
   */
  private void removeAccents(StringBuffer word)
  {
    for (int i = 0; i < word.length(); i++) {
      switch (word.charAt(i)) {
      case 'á':
      case 'à':
      case 'â':
      case 'ã':
        word.setCharAt(i, 'a');
        break;
      case 'é':
      case 'ê':
        word.setCharAt(i, 'e');
        break;
      case 'í':
        word.setCharAt(i, 'i');
        break;
      case 'ó':
      case 'ô':
      case 'õ':
        word.setCharAt(i, 'o');
        break;
      case 'ú':
      case 'ü':
        word.setCharAt(i, 'u');
        break;
      case 'ç'://adicionado
          word.setCharAt(i, 'c');
          break;
      }
    }
  }

  private boolean verbose = false;
  public void setVerbose()
  {
    verbose = true;
  }

  /**
   * Returns the stemmed version of the given word.
   *
   * @param word a string consisting of a single word
   */
  public String stem(String word) {
	
    StringBuffer w = new StringBuffer(word.toLowerCase());
    boolean changed;
    int n = w.length();
    if (n > 2) {
      if (w.charAt(n - 1) == 's') {
        if (applyRules(w, plural)) {
          if (verbose) System.out.print("PLU ");
        }
      } 
      //if(false){//if(false){ adicionado para ver se stemmer menos radical
      if (applyRules(w, adverb)) {
        if (verbose) System.out.print("ADV ");
      }
      n = w.length();
      if (w.charAt(n - 1) == 'a') {
        if (applyRules(w, feminine)) {
          if (verbose) System.out.print("FEM ");
        }
      }
      if (applyRules(w, augmentative)) {
        if (verbose) System.out.print("AUM ");
      }
      else {
        if (applyRules(w, diminutive)) {
          if (verbose) System.out.print("DIM ");
        }
      }
      if (applyRules(w, numeral)) {
        if (verbose) System.out.print("NUM ");
      }
      if (applyRules(w, noun)) {
        if (verbose) System.out.print("NOM ");
      }
      else {
        if (applyRules(w, verb)) {
          if (verbose) System.out.print("VER ");
        }
        else {
          if (applyRules(w, thematic_vowel)) {
            if (verbose) System.out.print("VOG ");
          }
        }
      }
      //}//add if(false)
      removeAccents(w);
    }
    return w.toString();
  }

  /**
   * Validate given rules
   */
  private void validateRules(Rule[] rules)
  {
    for (int i = 0; i < rules.length; i++) {
      rules[i].validate();
    }
  }

  /**
   * Validate all rules
   */
  public void validate()
  {
    validateRules(plural);
    validateRules(adverb);
    validateRules(feminine);
    validateRules(augmentative);
    validateRules(diminutive);
    validateRules(numeral);
    validateRules(noun);
    validateRules(verb);
    validateRules(thematic_vowel);
  }

  /**
   * Stems text coming into stdin and writes it to stdout.
   */
  public static void main(String[] args) {

    PortugueseStemmer ms = new PortugueseStemmer();
    ms.validate();
    if (args.length != 0) {
      System.out.println("-- verbose mode --");
      ms.setVerbose();
    }

    try {
      int num;
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in,Charset.forName("utf8")));
      StringBuffer wordBuffer = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null) {
          wordBuffer.append(line);
          System.out.println(line);
          if (wordBuffer.length() > 0) {
            System.out.println(ms.stem(wordBuffer.toString()));
            wordBuffer = new StringBuffer();
          }
          
        }
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}

/**
 * Base abstract rule
 */
abstract class Rule {
  public abstract boolean apply(StringBuffer word);
  public abstract void validate();
}

/**
 * Negative rule: replace suffix for just the matches given
 */
class NRule extends Rule {
  private String suffix;
  private String replacement;
  private String[] matches;

  public NRule(String suffix, String replacement, String[] matches)
  {
    this.suffix = suffix;
    this.replacement = replacement;
    this.matches = matches;
  }

  public boolean apply(StringBuffer word)
  {
    int wlen = word.length();
    int slen = suffix.length();

    // check whether the word ends with the suffix
    if (word.indexOf(suffix, wlen - slen) == -1) {
      return false;
    }

    // it ends with the suffix, should ensure it is one of the matches
    if (matches != null) {
      for (int i = 0; i < matches.length; i++) {
        if (matches[i].contentEquals(word)) {
          // strip suffix and append replacement if present
          word.replace(wlen - slen, wlen, replacement);
          return true;
        }
      }
    }

    // no matches found
    return false;
  }

  public void validate()
  {
    if (suffix == null || suffix.length() == 0 || replacement == null) {
      System.out.println("invalid NRule '" + suffix + "' -> '" + replacement + "'");
    }
    if (matches == null || matches.length == 0) {
      System.out.println("invalid NRule '" + suffix + "': match list cannot be null or empty");
    }
    for (int i = 0; i < matches.length; i++) {
      if (! matches[i].endsWith(suffix)) {
        System.out.println("invalid NRule '" + suffix + "': match '" + matches[i] + "' does not end with suffix");
      }
    }
  }
}

/**
 * Positive rule: replace suffix for all matching words, however taking into account the exception list
 */
class PRule extends Rule {
  private String suffix;
  private int minimum_length;
  private String replacement;
  private String[] exceptions;

  public PRule()
  {
  }
  public PRule(String suffix, int minimum_length)
  {
    this.suffix = suffix;
    this.minimum_length = minimum_length;
    this.replacement = "";
  }
  public PRule(String suffix, int minimum_length, String replacement)
  {
    this.suffix = suffix;
    this.minimum_length = minimum_length;
    this.replacement = replacement;
  }
  public PRule(String suffix, int minimum_length, String replacement, String[] exceptions)
  {
    this.suffix = suffix;
    this.minimum_length = minimum_length;
    this.replacement = replacement;
    this.exceptions = exceptions;
  }

  public boolean apply(StringBuffer word)
  {
    int wlen = word.length();
    int slen = suffix.length();

    // check whether the minimum stem length is satisfied
    if (wlen - slen < minimum_length) {
      return false;
    }

    // check whether the word ends with the suffix
    if (word.indexOf(suffix, wlen - slen) == -1) {
      return false;
    }

    // it ends with the suffix, but first check whether it is one of the exceptions
    if (exceptions != null) {
      for (int i = 0; i < exceptions.length; i++) {
        if (exceptions[i].charAt(0) == '*') {
          // match exception suffix
          if (word.indexOf(exceptions[i].substring(1), wlen - exceptions[i].length() - 1) != -1) {
            return false;
          }
        }
        else {
          // match exception
          if (exceptions[i].contentEquals(word)) {
            return false;
          }
        }
      }
    }

    // strip suffix and append replacement if present
    word.replace(wlen - slen, wlen, replacement);
    return true;
  }

  public void validate()
  {
    int slen = suffix.length();
    if (suffix == null || slen == 0 || minimum_length < 1 || minimum_length > 6 || replacement == null) {
      System.out.println("invalid PRule '" + suffix + "', " + minimum_length + " -> '" + replacement + "'");
    }
    if (exceptions != null) {
      if (exceptions.length == 0) {
        System.out.println("invalid PRule '" + suffix + "': exception list exists but is empty");
      }
      for (int i = 0; i < exceptions.length; i++) {
        if (! exceptions[i].endsWith(suffix)) {
          System.out.println("invalid PRule '" + suffix + "': exception '" + exceptions[i] + "' does not end with suffix");
        }
        if (exceptions[i].charAt(0) != '*' && exceptions[i].length() < slen + minimum_length) {
          System.out.println("invalid PRule '" + suffix + "': exception '" + exceptions[i] + "' does not have minimum length");
        }
      }
    }
  }
}

/**
 * Simple substitution rule
 */
class SRule extends Rule {
  private String word;
  private String substitution;

  public SRule(String word, String substitution)
  {
    this.word = word;
    this.substitution = substitution;
  }

  public boolean apply(StringBuffer word)
  {
    if (this.word.contentEquals(word)) {
      word.replace(0, word.length(), substitution);
      return true;
    }
    return false;
  }

  public void validate()
  {
    if (word == null || substitution == null || word.length() == 0 || substitution.length() == 0) {
      System.out.println("invalid SRule '" + word + "' -> '" + substitution + "'");
    }
  }
}
