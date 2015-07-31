package ch.chrissharkman.accessibility.rcp.application.poc.parts;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.chrissharkman.accessibility.rcp.application.poc.ContentManager;
import ch.chrissharkman.accessibility.rcp.base.AccessibleView;

/**
 * Part with a swt tree widget which contains an index parsed from an ncx/xml.
 * @author ChristianHeimann
 *
 */
public class IndexViewPart implements AccessibleView {
	
	private static Logger logger = Logger.getLogger(IndexViewPart.class);
	private Composite viewComposite;
	

	@PostConstruct
	public void createComposite(Composite parent) {
		this.viewComposite = parent;
		
		parent.setLayout(new FillLayout());
		createTree(parent, "content/utopia.ncx");
	}
	
	/**
	 * Function to create index tree of a ncx (epub index) file, beginning with the title of the publication. The recursivity respects the ncx-2005-1.dtd schema.
	 * @param parent the composite parent where to set the tree.
	 */
	public void createTree(Composite parent, String path) {
		Document docXml = ContentManager.instance().getXml(path);
		Tree tree = new Tree (parent, SWT.BORDER);
		TreeItem treeItem = new TreeItem(tree, 0);
		treeItem.setText(docXml.getElementsByTagName("docTitle").item(0).getTextContent());
		
		NodeList nodeList = docXml.getElementsByTagName("navMap");
		if (nodeList.getLength() == 1) {
			Element childNodes = (Element) nodeList.item(0).getChildNodes();
			if (childNodes.getFirstChild() != null) {				
				navPointRecursivity(childNodes, treeItem);
			}
		}
		logger.info("Tree creation finished");
	}	
	
	/**
	 * Recursive Function to create treeItems with text from the nodes TextContent. The recursivity respects the ncx-2005-1.dtd schema.
	 * @param nodes an Element that contains Nodes.
	 * @param treeItem the treeItem of the upper hierarchy, where to set the next TreeItem.
	 */
	private void navPointRecursivity(Element nodes, TreeItem treeItem) {
		if (nodes.getFirstChild() != null) {
			Node node = nodes.getFirstChild();
			do {
				if (node.getNodeName().contentEquals("navPoint")) {
					Element childNodes = (Element) node.getChildNodes();
					TreeItem childTreeItem = new TreeItem(treeItem, 0);
					navPointRecursivity(childNodes, childTreeItem);
				} else if (node.getNodeName().contentEquals("navLabel")) {
					treeItem.setText(node.getTextContent());
				}
				if (node.getNextSibling() != null) {
					node = node.getNextSibling();
				}
			} while (node.getNextSibling() != null);
		}
	}

	@Override
	public Composite getViewComposite() {
		return this.viewComposite;
	}
	
	

}
