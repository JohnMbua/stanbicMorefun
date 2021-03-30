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

public class MinstList {

	private String mindesc;
    private String minamo;
    private String mindate;






	public MinstList(String mindesc, String minam,String mdate) {
		this.minamo = minam;
this.mindesc = mindesc;
        this.mindate = mdate;

	}



	public String getBenName() {
		return mindesc;
	}
	public void setBenName(String bname) {
		this.mindesc = bname;
	}

	public String getBenmob() {
		return minamo;
	}
	public void setBenmob(String bmob) {
		this.minamo = bmob;
	}

    public String getMinDate() {
        return mindate;
    }
    public void setMinDate(String bmob) {
        this.mindate = bmob;
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
