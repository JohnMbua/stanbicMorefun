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

public class InboxList {

	private String benname;
    private String benmob;






	public InboxList(String benname, String benmob) {
		this.benname = benname;
this.benmob = benmob;

	}



	public String getBenName() {
		return benname;
	}
	public void setBenName(String bname) {
		this.benname = bname;
	}

	public String getBenmob() {
		return benmob;
	}
	public void setBenmob(String bmob) {
		this.benmob = bmob;
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
