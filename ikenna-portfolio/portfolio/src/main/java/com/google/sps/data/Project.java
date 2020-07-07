// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

/**
* The Project class stores information that represents how the projects container
* will look like.
*/
public class Project {
  private final String name;
  private final String imageHTML;
  private final String desc;
  private final String links;
  
  public Project(String name, String img1, String img2, String img3, String desc, String links) {
    this(name, img1, img2, img3, desc, links, 3);
  }

  public Project(String name, String img1, String img2, String img3, String desc, String links, int sections) {
    this.name = name;
    this.imageHTML = getImageHTML(sections, new String[] {img1, img2, img3});
    this.desc = desc;
    this.links = links;
  }

  private String getImageHTML(int sections, String[] src) {
    String html = "";

    /*Different formats because 3 sections requires "col-sm-4 whereas 1 section doesn't"*/
    if (sections == 3) {
      for(int i = 0; i < sections; i++)
        html += "<div class=\"col-sm-4\"><img class=\"image-format\" src=\"" + src[i] + "\"></div>";
    }
    else {
      html = "<img class=\"image-format\" src=\"" + src[0] + "\">";
    }
    return html;
  }
}
