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

package com.google.sps.login;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Wrapper class to make authentication testable. */
public class WebLogin implements LoginInterface {

  @Override
  public boolean isLoggedIn() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.isUserLoggedIn();
  }

  @Override
  public String getLoginRedirect(String redirect) {
    UserService userService = UserServiceFactory.getUserService();
    return userService.isUserLoggedIn() ? userService.createLogoutURL(redirect) : userService.createLoginURL(redirect);
  }
  
  @Override
  public String getEmail() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.isUserLoggedIn() ? userService.getCurrentUser().getEmail() : "";
  }
}
