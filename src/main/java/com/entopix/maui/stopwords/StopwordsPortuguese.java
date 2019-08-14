/**
 * StopwordsPortuguese.java
 * Copyright (C) 2004-2005 Maria Abadia Lacerda Dias
 * Copyright (C) 2019-2020 Renato Correa
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.entopix.maui.stopwords;

import java.util.*;


/**
 * Class that can test whether a given string is a stopword.
 * Lowercases all words before the test.
 *
 * @author Maria Abadia Lacerda Dias (mald@univates.br)
 * @author Renato Correa (renatocorrea@gmail.com)
 * @author Rahmon Jorge
 * @version 1.1
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StopwordsPortuguese extends Stopwords {
	
	public StopwordsPortuguese() {
		super(new ArrayList<String>(m_Stopwords.keySet()));
	}

	private static final long serialVersionUID = 1L;

  /** The hashtable containing the list of stopwords */
  private static Hashtable m_Stopwords = null;

  static {
    if (m_Stopwords == null) {
      m_Stopwords = new Hashtable();
      Double dummy = new Double(0);

      m_Stopwords.put("a", dummy);
      m_Stopwords.put("abaixo", dummy);
      m_Stopwords.put("acaso", dummy);
      m_Stopwords.put("acerca", dummy);
      m_Stopwords.put("acima", dummy);
      m_Stopwords.put("acinte", dummy);
      m_Stopwords.put("adentro", dummy);
      m_Stopwords.put("adiante", dummy);
      m_Stopwords.put("adrede", dummy);
      m_Stopwords.put("afinal", dummy);
      m_Stopwords.put("afora", dummy);
      m_Stopwords.put("agora", dummy);
      m_Stopwords.put("ainda", dummy);
      m_Stopwords.put("algo", dummy);
      m_Stopwords.put("algum", dummy);
      m_Stopwords.put("alguma", dummy);
      m_Stopwords.put("algumas", dummy);
      m_Stopwords.put("alguns", dummy);
      m_Stopwords.put("algures", dummy);
      m_Stopwords.put("alguém", dummy);
      m_Stopwords.put("alhures", dummy);
      m_Stopwords.put("ali", dummy);
      m_Stopwords.put("aliás", dummy);
      m_Stopwords.put("além", dummy);
      m_Stopwords.put("amanhã", dummy);
      m_Stopwords.put("amiúde", dummy);
      m_Stopwords.put("ante", dummy);
      m_Stopwords.put("anteontem", dummy);
      m_Stopwords.put("antes", dummy);
      m_Stopwords.put("ao", dummy);
      m_Stopwords.put("aonde", dummy);
      m_Stopwords.put("aos", dummy);
      m_Stopwords.put("apenas", dummy);
      m_Stopwords.put("apesar", dummy);
      m_Stopwords.put("após", dummy);
      m_Stopwords.put("aquela", dummy);
      m_Stopwords.put("aquelas", dummy);
      m_Stopwords.put("aquele", dummy);
      m_Stopwords.put("aqueles", dummy);
      m_Stopwords.put("aqui", dummy);
      m_Stopwords.put("aquilo", dummy);
      m_Stopwords.put("aquém", dummy);
      m_Stopwords.put("as", dummy);
      m_Stopwords.put("assaz", dummy);
      m_Stopwords.put("assim", dummy);
      m_Stopwords.put("através", dummy);
      m_Stopwords.put("atrás", dummy);
      m_Stopwords.put("até", dummy);
      m_Stopwords.put("aí", dummy);
      m_Stopwords.put("à", dummy);
      m_Stopwords.put("àquela", dummy);
      m_Stopwords.put("àquelas", dummy);
      m_Stopwords.put("àquele", dummy);
      m_Stopwords.put("àqueles", dummy);
      m_Stopwords.put("àquilo", dummy);
      m_Stopwords.put("às", dummy);
      m_Stopwords.put("b", dummy);
      m_Stopwords.put("buscada", dummy);
      m_Stopwords.put("buscadas", dummy);
      m_Stopwords.put("buscado", dummy);
      m_Stopwords.put("buscados", dummy);
      m_Stopwords.put("buscaram", dummy);
      m_Stopwords.put("buscava", dummy);
      m_Stopwords.put("buscavam", dummy);
      m_Stopwords.put("buscou", dummy);
      m_Stopwords.put("c", dummy);
      m_Stopwords.put("cada", dummy);
      m_Stopwords.put("cedo", dummy);
      m_Stopwords.put("com", dummy);
      m_Stopwords.put("comigo", dummy);
      m_Stopwords.put("como", dummy);
      m_Stopwords.put("conosco", dummy);
      m_Stopwords.put("conquanto", dummy);
      m_Stopwords.put("consigo", dummy);
      m_Stopwords.put("contanto", dummy);
      m_Stopwords.put("contigo", dummy);
      m_Stopwords.put("contra", dummy);
      m_Stopwords.put("contudo", dummy);
      m_Stopwords.put("convosco", dummy);
      m_Stopwords.put("cuja", dummy);
      m_Stopwords.put("cujas", dummy);
      m_Stopwords.put("cujo", dummy);
      m_Stopwords.put("cujos", dummy);
      m_Stopwords.put("cá", dummy);
      m_Stopwords.put("d", dummy);
      m_Stopwords.put("da", dummy);
      m_Stopwords.put("daquela", dummy);
      m_Stopwords.put("daquelas", dummy);
      m_Stopwords.put("daquele", dummy);
      m_Stopwords.put("daqueles", dummy);
      m_Stopwords.put("daquilo", dummy);
      m_Stopwords.put("das", dummy);
      m_Stopwords.put("de", dummy);
      m_Stopwords.put("debaixo", dummy);
      m_Stopwords.put("debalde", dummy);
      m_Stopwords.put("decerto", dummy);
      m_Stopwords.put("defronte", dummy);
      m_Stopwords.put("dela", dummy);
      m_Stopwords.put("delas", dummy);
      m_Stopwords.put("dele", dummy);
      m_Stopwords.put("deles", dummy);
      m_Stopwords.put("demais", dummy);
      m_Stopwords.put("dentro", dummy);
      m_Stopwords.put("depois", dummy);
      m_Stopwords.put("depressa", dummy);
      m_Stopwords.put("desde", dummy);
      m_Stopwords.put("dessa", dummy);
      m_Stopwords.put("dessas", dummy);
      m_Stopwords.put("desse", dummy);
      m_Stopwords.put("desses", dummy);
      m_Stopwords.put("desta", dummy);
      m_Stopwords.put("destas", dummy);
      m_Stopwords.put("deste ", dummy);
      m_Stopwords.put("destes", dummy);
      m_Stopwords.put("detrás", dummy);
      m_Stopwords.put("devagar", dummy);
      m_Stopwords.put("deveras", dummy);
      m_Stopwords.put("diante", dummy);
      m_Stopwords.put("disso", dummy);
      m_Stopwords.put("disto", dummy);
      m_Stopwords.put("do", dummy);
      m_Stopwords.put("donde", dummy);
      m_Stopwords.put("dos", dummy);
      m_Stopwords.put("dum", dummy);
      m_Stopwords.put("duma", dummy);
      m_Stopwords.put("dumas", dummy);
      m_Stopwords.put("duns", dummy);
      m_Stopwords.put("durante", dummy);
      m_Stopwords.put("e", dummy);
      m_Stopwords.put("eis", dummy);
      m_Stopwords.put("ela", dummy);
      m_Stopwords.put("elas", dummy);
      m_Stopwords.put("ele", dummy);
      m_Stopwords.put("eles", dummy);
      m_Stopwords.put("em", dummy);
      m_Stopwords.put("embaixo", dummy);
      m_Stopwords.put("embora", dummy);
      m_Stopwords.put("enfim", dummy);
      m_Stopwords.put("enquanto", dummy);
      m_Stopwords.put("entanto", dummy);
      m_Stopwords.put("entre", dummy);
      m_Stopwords.put("entrementes", dummy);
      m_Stopwords.put("entretanto", dummy);
      m_Stopwords.put("então", dummy);
      m_Stopwords.put("essa", dummy);
      m_Stopwords.put("essas", dummy);
      m_Stopwords.put("esse", dummy);
      m_Stopwords.put("esses", dummy);
      m_Stopwords.put("esta", dummy);
      m_Stopwords.put("está", dummy);
      m_Stopwords.put("estão", dummy);
      m_Stopwords.put("estaria", dummy);
      m_Stopwords.put("estariam", dummy);
      m_Stopwords.put("estava", dummy);
      m_Stopwords.put("estavam", dummy);
      m_Stopwords.put("estas", dummy);
      m_Stopwords.put("este", dummy);
      m_Stopwords.put("estes", dummy);
      m_Stopwords.put("eu", dummy);
      m_Stopwords.put("exceto", dummy);
      m_Stopwords.put("f", dummy);
      m_Stopwords.put("fora", dummy);
      m_Stopwords.put("g", dummy);
      m_Stopwords.put("h", dummy);
      m_Stopwords.put("hoje", dummy);
      m_Stopwords.put("i", dummy);
      m_Stopwords.put("inclusive", dummy);
      m_Stopwords.put("isso", dummy);
      m_Stopwords.put("isto", dummy);
      m_Stopwords.put("j", dummy);
      m_Stopwords.put("jamais", dummy);
      m_Stopwords.put("já", dummy);
      m_Stopwords.put("k", dummy);
      m_Stopwords.put("l", dummy);
      m_Stopwords.put("la", dummy);
      m_Stopwords.put("las", dummy);
      m_Stopwords.put("levanta", dummy);
      m_Stopwords.put("levantada", dummy);
      m_Stopwords.put("levantadas", dummy);
      m_Stopwords.put("levantado", dummy);
      m_Stopwords.put("levantados", dummy);
      m_Stopwords.put("levantam", dummy);
      m_Stopwords.put("lhe", dummy);
      m_Stopwords.put("lhes", dummy);
      m_Stopwords.put("lo", dummy);
      m_Stopwords.put("logo", dummy);
      m_Stopwords.put("longe", dummy);
      m_Stopwords.put("los", dummy);
      m_Stopwords.put("lá", dummy);
      m_Stopwords.put("m", dummy);
      m_Stopwords.put("mais", dummy);
      m_Stopwords.put("mal", dummy);
      m_Stopwords.put("marcada", dummy);
      m_Stopwords.put("marcadas", dummy);
      m_Stopwords.put("marcado", dummy);
      m_Stopwords.put("marcados", dummy);
      m_Stopwords.put("marcam", dummy);
      m_Stopwords.put("marcaram", dummy);
      m_Stopwords.put("mas", dummy);
      m_Stopwords.put("me", dummy);
      m_Stopwords.put("meu", dummy);
      m_Stopwords.put("meus", dummy);
      m_Stopwords.put("mim", dummy);
      m_Stopwords.put("minha", dummy);
      m_Stopwords.put("minhas", dummy);
      m_Stopwords.put("muita", dummy);
      m_Stopwords.put("muitas", dummy);
      m_Stopwords.put("muito ", dummy);
      m_Stopwords.put("muitos", dummy);
      m_Stopwords.put("n", dummy);
      m_Stopwords.put("na", dummy);
      m_Stopwords.put("nada", dummy);
      m_Stopwords.put("naquela", dummy);
      m_Stopwords.put("naquelas", dummy);
      m_Stopwords.put("naquele", dummy);
      m_Stopwords.put("naqueles", dummy);
      m_Stopwords.put("naquilo", dummy);
      m_Stopwords.put("nas", dummy);
      m_Stopwords.put("nela", dummy);
      m_Stopwords.put("nelas", dummy);
      m_Stopwords.put("nele", dummy);
      m_Stopwords.put("neles", dummy);
      m_Stopwords.put("nem", dummy);
      m_Stopwords.put("nenhum", dummy);
      m_Stopwords.put("nenhuma", dummy);
      m_Stopwords.put("nenhumas", dummy);
      m_Stopwords.put("nenhuns", dummy);
      m_Stopwords.put("nenhures", dummy);
      m_Stopwords.put("nessa", dummy);
      m_Stopwords.put("nessas", dummy);
      m_Stopwords.put("nesse", dummy);
      m_Stopwords.put("nesses", dummy);
      m_Stopwords.put("nesta", dummy);
      m_Stopwords.put("nestas", dummy);
      m_Stopwords.put("neste", dummy);
      m_Stopwords.put("nestes", dummy);
      m_Stopwords.put("ninguém", dummy);
      m_Stopwords.put("nisso", dummy);
      m_Stopwords.put("nisto", dummy);
      m_Stopwords.put("no", dummy);
      m_Stopwords.put("nos", dummy);
      m_Stopwords.put("nossa", dummy);
      m_Stopwords.put("nossas", dummy);
      m_Stopwords.put("nosso", dummy);
      m_Stopwords.put("nossos", dummy);
      m_Stopwords.put("num", dummy);
      m_Stopwords.put("numa", dummy);
      m_Stopwords.put("numas", dummy);
      m_Stopwords.put("nunca", dummy);
      m_Stopwords.put("nuns", dummy);
      m_Stopwords.put("não", dummy);
      m_Stopwords.put("nós", dummy);
      m_Stopwords.put("o", dummy);
      m_Stopwords.put("onde", dummy);
      m_Stopwords.put("ontem", dummy);
      m_Stopwords.put("ora", dummy);
      m_Stopwords.put("os", dummy);
      m_Stopwords.put("ou", dummy);
      m_Stopwords.put("outra", dummy);
      m_Stopwords.put("outras", dummy);
      m_Stopwords.put("outrem", dummy);
      m_Stopwords.put("outro", dummy);
      m_Stopwords.put("outrora", dummy);
      m_Stopwords.put("outros", dummy);
      m_Stopwords.put("p", dummy);
      m_Stopwords.put("para", dummy);
      m_Stopwords.put("pela", dummy);
      m_Stopwords.put("pelas", dummy);
      m_Stopwords.put("pelo", dummy);
      m_Stopwords.put("pelos", dummy);
      m_Stopwords.put("per", dummy);
      m_Stopwords.put("perante", dummy);
      m_Stopwords.put("perto", dummy);
      m_Stopwords.put("pois", dummy);
      m_Stopwords.put("por", dummy);
      m_Stopwords.put("porquanto", dummy);
      m_Stopwords.put("porque", dummy);
      m_Stopwords.put("portanto", dummy);
      m_Stopwords.put("porventura", dummy);
      m_Stopwords.put("porém", dummy);
      m_Stopwords.put("pouca", dummy);
      m_Stopwords.put("poucas", dummy);
      m_Stopwords.put("pouco", dummy);
      m_Stopwords.put("poucos", dummy);
      m_Stopwords.put("precisa", dummy);
      m_Stopwords.put("precisam", dummy);
      m_Stopwords.put("precisaram", dummy);
      m_Stopwords.put("precisou", dummy);
      m_Stopwords.put("q", dummy);
      m_Stopwords.put("quais", dummy);
      m_Stopwords.put("qual ", dummy);
      m_Stopwords.put("quando", dummy);
      m_Stopwords.put("quase", dummy);
      m_Stopwords.put("que", dummy);
      m_Stopwords.put("quem", dummy);
      m_Stopwords.put("quiçá", dummy);
      m_Stopwords.put("r", dummy);
      m_Stopwords.put("realiza", dummy);
      m_Stopwords.put("realizada", dummy);
      m_Stopwords.put("realizadas", dummy);
      m_Stopwords.put("realizado", dummy);
      m_Stopwords.put("realizados", dummy);
      m_Stopwords.put("realizar", dummy);
      m_Stopwords.put("realizaram", dummy);
      m_Stopwords.put("realizou", dummy);
      m_Stopwords.put("s", dummy);
      m_Stopwords.put("se", dummy);
      m_Stopwords.put("sem", dummy);
      m_Stopwords.put("sempre", dummy);
      m_Stopwords.put("seu", dummy);
      m_Stopwords.put("seus", dummy);
      m_Stopwords.put("si ", dummy);
      m_Stopwords.put("sim", dummy);
      m_Stopwords.put("sob", dummy);
      m_Stopwords.put("sobre", dummy);
      m_Stopwords.put("somente", dummy);
      m_Stopwords.put("sua", dummy);
      m_Stopwords.put("suas", dummy);
      m_Stopwords.put("t", dummy);
      m_Stopwords.put("talvez", dummy);
      m_Stopwords.put("também", dummy);
      m_Stopwords.put("tampouco", dummy);
      m_Stopwords.put("tanta", dummy);
      m_Stopwords.put("tantas", dummy);
      m_Stopwords.put("tanto", dummy);
      m_Stopwords.put("tantos", dummy);
      m_Stopwords.put("tarde", dummy);
      m_Stopwords.put("te", dummy);
      m_Stopwords.put("teu", dummy);
      m_Stopwords.put("teus", dummy);
      m_Stopwords.put("ti", dummy);
      m_Stopwords.put("todavia", dummy);
      m_Stopwords.put("trás", dummy);
      m_Stopwords.put("tu", dummy);
      m_Stopwords.put("tua", dummy);
      m_Stopwords.put("tuas", dummy);
      m_Stopwords.put("tudo", dummy);
      m_Stopwords.put("tão", dummy);
      m_Stopwords.put("u", dummy);
      m_Stopwords.put("um", dummy);
      m_Stopwords.put("uma", dummy);
      m_Stopwords.put("umas", dummy);
      m_Stopwords.put("uns", dummy);
      m_Stopwords.put("v", dummy);
      m_Stopwords.put("vos", dummy);
      m_Stopwords.put("vossa", dummy);
      m_Stopwords.put("vossas", dummy);
      m_Stopwords.put("vosso", dummy);
      m_Stopwords.put("vossos", dummy);
      m_Stopwords.put("vós", dummy);
      m_Stopwords.put("w", dummy);
      m_Stopwords.put("x", dummy);
      m_Stopwords.put("y", dummy);
      m_Stopwords.put("z", dummy);
    }
  }

  /**
   * Returns true if the given string is a stop word.
   */
  public boolean isStopwordL(String str) {
    return m_Stopwords.containsKey(str.toLowerCase());
  }
  
  @Override
  public boolean isStopword(String word) {
      // make sure word is in lowercase
      return super.isStopword(word.toLowerCase());
  }

}
