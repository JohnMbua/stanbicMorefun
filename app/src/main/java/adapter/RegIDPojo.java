/*
 * Copyright (C) 2012 jfrankie (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package adapter;

public class RegIDPojo {

	private int id;
    private String appid;





	public RegIDPojo() {


	}

	public RegIDPojo(int id, String appid) {
		this.id = id;
this.appid = appid;

	}



	public int getID() {
		return id;
	}
	public void setID(int accid) {
		this.id = accid;
	}

	public String getAppID() {
		return appid;
	}
	public void setAppid(String curr) {
		this.appid = curr;
	}

}
