package outputModules.csv.exporters;

import java.util.Map;

import cfg.CFG;
import cfg.CFGEdge;
import cfg.nodes.ASTNodeContainer;
import cfg.nodes.AbstractCFGNode;
import cfg.nodes.CFGEntryNode;
import cfg.nodes.CFGExitNode;
import cfg.nodes.CFGNode;
import databaseNodes.EdgeTypes;
import databaseNodes.NodeKeys;
import outputModules.common.CFGExporter;
import outputModules.common.Writer;

public class CSVCFGExporter extends CFGExporter
{

	@Override
	protected void writeCFGNode(CFGNode statement,
			Map<String, Object> properties)
	{
		properties.put(NodeKeys.FUNCTION_ID,
				String.format("%d", Writer.getIdForObject(currentFunction)));
		Writer.addNode(statement, properties);
	}

	@Override
	protected void addFlowToLink(Object srcBlock, Object dstBlock,
			Map<String, Object> properties)
	{
		long srcId = Writer.getIdForObject(srcBlock);
		long dstId = Writer.getIdForObject(dstBlock);
		Writer.addEdge(srcId, dstId, properties, EdgeTypes.FLOWS_TO);
	}

	/**
	 * Simple method that takes a CFG and writes out the edges.
	 */
	public void writeCFGEdges(CFG cfg) {

		for( CFGEdge cfgEdge : cfg.getEdges())	{

			CFGNode src = cfgEdge.getSource();
			CFGNode dst = cfgEdge.getDestination();
			
			// skip blocks that are neither entry, exit nor AST nodes
			// (e.g., artificial [ERROR] blocks and such generated by the CFG factory)
			if( (src instanceof ASTNodeContainer || src instanceof CFGEntryNode || src instanceof CFGExitNode)
					&& (dst instanceof ASTNodeContainer || dst instanceof CFGEntryNode || dst instanceof CFGExitNode)) {
				
				// CFG nodes that are AST node containers have their ids stored in their AST node;
				// abstract nodes such as entry or exit nodes have their id set internally.
				Long srcId = (src instanceof ASTNodeContainer) ? ((ASTNodeContainer)src).getASTNode().getNodeId()
						: ((AbstractCFGNode)src).getNodeId();
				Long dstId = (dst instanceof ASTNodeContainer) ? ((ASTNodeContainer)dst).getASTNode().getNodeId()
						: ((AbstractCFGNode)dst).getNodeId();
				
				Writer.setIdForObject(src, srcId);
				Writer.setIdForObject(dst, dstId);
				addFlowToLink( src, dst, cfgEdge.getProperties());
			}
		}
		// clean up
		Writer.reset();
	}
	
}
