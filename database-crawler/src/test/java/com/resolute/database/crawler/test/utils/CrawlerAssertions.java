package com.resolute.database.crawler.test.utils;

import java.util.List;

import com.resolute.database.crawler.model.Node;

public class CrawlerAssertions {

  public static NodeListAssertions with(List<Node> sorted) {
    return new NodeListAssertions(sorted);
  }

}
