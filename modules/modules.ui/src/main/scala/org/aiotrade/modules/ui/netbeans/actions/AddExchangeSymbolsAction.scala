/*
 * Copyright (c) 2006-2007, AIOTrade Computing Co. and Contributors
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *    
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *    
 *  o Neither the name of AIOTrade Computing Co. nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.aiotrade.modules.ui.netbeans.actions;

import org.aiotrade.lib.math.timeseries.TFreq
import org.aiotrade.lib.securities.Exchange
import org.aiotrade.lib.securities.dataserver.QuoteContract
import org.aiotrade.modules.ui.netbeans.nodes.SymbolNodes
import org.aiotrade.modules.ui.netbeans.windows.ExplorerTopComponent
import org.openide.loaders.DataFolder
import org.openide.util.HelpCtx
import org.openide.util.actions.CallableSystemAction

/**
 *
 * @author Caoyuan Deng
 */
class AddExchangeSymbolsAction extends CallableSystemAction {
    
  def performAction {
    java.awt.EventQueue.invokeLater(new Runnable {
        def run {
          val explorerTc = ExplorerTopComponent()
          explorerTc.requestActive
          
          val rootNode = explorerTc.getExplorerManager.getRootContext
          val rootFolder = rootNode.getLookup.lookup(classOf[DataFolder])
          val exchangeFolder = DataFolder.create(rootFolder, Exchange.SS.code)

          // expand root node
          explorerTc.getExplorerManager.setExploredContext(rootNode)
                
          // add symbols in market folder
          val quoteContract = createQuoteContract
          // quoteContract may bring in more than one symbol, should process it later
          for (symbol <- Exchange.symbolsOf(Exchange.SS)) {
            // dataSourceDescriptor may has been set to more than one symbols, process it here
            quoteContract.symbol = symbol
                    
            SymbolNodes.createSymbolXmlFile(exchangeFolder, symbol, quoteContract)
          }
        }
      })
        
  }

  private def createQuoteContract = {
    val quoteContract = new QuoteContract
    quoteContract.active = true
    quoteContract.serviceClassName = "org.aiotrade.lib.dataserver.yahoo.YahooQuoteServer"
    //quoteContract.beginDate_$eq((Date) fromDateField.getValue());
    //quoteContract.endDate_$eq((Date) toDateField.getValue());
    //quoteContract.urlString_$eq(pathField.getText().trim());

    quoteContract.freq = TFreq.DAILY

    quoteContract.refreshable = false
    
    quoteContract
  }
    
  def getName = {
    "Add Exchange Symbols"
  }
    
  def getHelpCtx: HelpCtx = {
    HelpCtx.DEFAULT_HELP
  }
    
  override protected def iconResource: String = {
    "org/aiotrade/modules/ui/netbeans/resources/newSymbol.gif"
  }
    
  override protected def asynchronous: Boolean = {
    false
  }
    
    
}

