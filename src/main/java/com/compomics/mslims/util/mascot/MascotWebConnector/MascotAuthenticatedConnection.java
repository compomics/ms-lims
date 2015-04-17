package com.compomics.mslims.util.mascot.MascotWebConnector;

/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */
import java.io.IOException;
import java.util.Properties;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * MascotAuthenticatedConnection code for the mascot server
 *
 * @author Toorn101
 */
public class MascotAuthenticatedConnection {

   private MultiThreadedHttpConnectionManager connectionManager =
            new MultiThreadedHttpConnectionManager();
   private HttpClient client = new HttpClient(connectionManager);
   private Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "IdentificationGUI.properties");

    /**
     * Create a login post method for the mascot server
     * @param username
     * @param password
     * @return the prepared post method
     * @throws java.lang.IllegalArgumentException
     */
    private PostMethod loginMethod(String username, String password) {

        PostMethod authpost = new PostMethod("/mascot/cgi/login.pl");
        // Prepare login parameters
        NameValuePair[] arguments = {
            new NameValuePair("action", "login"),
            new NameValuePair("username", username),
            new NameValuePair("password", password),
            new NameValuePair("display", "logout_prompt"),
            new NameValuePair("savecookie", "1"),
            new NameValuePair("onerrdisplay", "login_prompt"),};
        authpost.setRequestBody(arguments);
        return authpost;
    }

    /**
     * create a post method with parameters for logging out of the mascot server
     * @return the logout post method
     * @throws java.lang.IllegalArgumentException
     */
    private PostMethod logoutMethod() {

        PostMethod authpost = new PostMethod("/mascot/cgi/login.pl");
        // Prepare login parameters
        NameValuePair[] parameters = {
            new NameValuePair("action", "logout"),
            new NameValuePair("onerrdisplay", "nothing")};

        authpost.setRequestBody(parameters);

        return authpost;
    }

    /**
     * create a web client, execute the command and set cookies
     * @param method
     * @throws org.apache.commons.httpclient.HttpException
     * @throws java.io.IOException
     */
    public int executeMethod(PostMethod method) throws IOException {
        if(lConnectionProperties.contains("mascotserverport")) {
        client.getHostConfiguration().setHost(lConnectionProperties.getProperty("mascotserverlocation"), Integer.parseInt(lConnectionProperties.getProperty("mascotserverport")));
        } else{
            client.getHostConfiguration().setHost(lConnectionProperties.getProperty("mascotserverlocation"));
        }
        client.getParams().setCookiePolicy(CookiePolicy.DEFAULT);
        client.getParams().setConnectionManagerTimeout(0);
        int status = client.executeMethod(method);
        method.releaseConnection();

        return status;
    }

    /**
     * login with username from the settings file
     * @return login success
     */
    public boolean login() throws IOException {
        return login(lConnectionProperties.getProperty("mascotloginname"), lConnectionProperties.getProperty("mascotserverpassword"));
    }

    /**
     * login with both username and password
     * @param username
     * @param password
     * @return login success
     */
    public boolean login(String username, String password) throws IOException {
        executeMethod(logoutMethod());
        executeMethod(loginMethod(username, password));
        return areCredentialsPresent();
    }

    /**
     * logout from the server
     * @return
     * @throws org.apache.commons.httpclient.HttpException
     * @throws java.io.IOException
     */
    public boolean logout() throws IOException {

        executeMethod(logoutMethod());
        return !areCredentialsPresent();
    }

    /**
     * check if authorization has been done
     * @throws java.lang.Exception
     */
    public boolean areCredentialsPresent() {
        Cookie[] cookies = client.getState().getCookies();
        if (cookies == null) {
            return false;
        }
        boolean loggedIn = false;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("MASCOT_SESSION")) {
                if (!cookie.getValue().isEmpty()) {
                    loggedIn = true;
                }
            }
        }
        return loggedIn;
    }

    public HttpClient getClient() {
        return client;
    }
}
