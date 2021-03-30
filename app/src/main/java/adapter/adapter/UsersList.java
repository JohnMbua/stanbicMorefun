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
package adapter.adapter;

public class UsersList {

	private String username;
    private String usergroup;
	private String storeid;




	public UsersList(String username,String usergroup, String storeid) {
		this.storeid = storeid;
this.usergroup = usergroup;
		this.username = username;


	}



	public String getUserName() {
		return username;
	}
	public void setUserName(String bname) {
		this.username = bname;
	}

	public String getStoreid() {
		return storeid;
	}
	public void setStoreid(String bmob) {
		this.storeid = bmob;
	}


	public String GetUserGroup() {
		return usergroup;
	}
	public void setUsergroup(String bmob) {
		this.usergroup = bmob;
	}
/*
    public String getAmo() {
        return amount;
    }
    public void setAmo(String amon) {
        this.amount = amon;
    }

    public String getAcctype() {
        return acctype;
    }
    public void setAcctype(String acct) {
        this.acctype = acct;
    }*/

	
	
}
