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

import java.io.Serializable;

/**
* The User class stores user information.
*/
public class User implements Serializable {
  private final String email;
  private final boolean admin;
  private final String nickname;
  
  public User(String email, boolean admin) {
    this(email, admin, getLdap(email));
  }

  public User(String email, boolean admin, String nickname) {
    this.email = email;
    this.admin = admin;
    this.nickname = nickname;
  }

  private static String getLdap(String email) {
    int upto = email.indexOf("@"); 
    return upto != -1 ? email.substring(0 , upto) : "";
  }

  public boolean isAdmin() {
    return admin;
  }

  public String getNickname() {
    return nickname;
  }

  public String getEmail() {
    return email;
  }
}
