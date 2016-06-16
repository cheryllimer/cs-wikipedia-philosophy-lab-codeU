package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;
import org.omg.CORBA.Current;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 *
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 *
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException
	{
		String base = "https://en.wikipedia.org";
		String link = "/wiki/Java_(programming_language)";
		String url = base.concat(link);

		ArrayList<String> visited = new ArrayList<String>();
		Elements links;
		Element Para;

		do
		{
			int pIndex = 0;
			Elements paragraphs = wf.fetchWikipedia(url);
			visited.add(url);
			do
			{
				Para = paragraphs.get(pIndex);
				pIndex++;
				links = Para.select("a");
			}while(links.isEmpty() && pIndex < paragraphs.size());

			if(pIndex >= paragraphs.size())
			{
				System.out.println("There is no link on the page " + url);
				System.out.println("\n" + visited.toString());
				return;
			}

			int parenth = 0;
			Iterable<Node> iter = new WikiNodeIterable(Para);
			boolean found = false;

			for (Node node: iter)
			{
				if (node instanceof TextNode)
				{
					if(node.toString().contains("("))
						parenth++;
					if(node.toString().contains(")"))
						parenth--;
				}

				if(links.contains(node) && parenth%2 == 0)
				{
					link = node.toString();

					int index = link.indexOf("\"", 10);
					link = link.substring(9, index);

					url = base.concat(link);
					if(visited.contains(url))
					{
						System.out.println("Web page already visited: " + url);
						System.out.println("\n" + visited.toString());
						return;
					}
					found = true;
				}

				if(found)
					break;
	        }
		} while(!(url.equals("https://en.wikipedia.org/wiki/Philosophy")));

		visited.add("https://en.wikipedia.org/wiki/Philosophy");
		System.out.println("Found!\n" + visited.toString());
	}

}
