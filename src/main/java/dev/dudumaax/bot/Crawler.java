package dev.dudumaax.bot;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	public String returnImage(String url) {
		if (!url.startsWith("https://prnt.sc/"))
			return url;
		try {
			Document doc = Jsoup.connect(url).get();
			Elements divs = doc.getElementsByTag("div");
			for (Element el : divs) {
				if (el.hasClass("image-constrain js-image-wrap")) {
					Elements div2 = el.getAllElements();
					for (Element divfinalmaybe : div2) {
						if (divfinalmaybe.hasClass("image-container image__pic js-image-pic")) {
							Elements imgs = divfinalmaybe.getAllElements();
							for (Element img : imgs) {
								if (img.hasClass("no-click screenshot-image")) {
									String src = img.absUrl("src");
									return src;
								}
							}
						}
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
