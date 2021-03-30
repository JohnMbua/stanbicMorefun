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

public class NewAccountList {

	private String accid;
    private String curr;
    private String amount;
    private String acctype;
    private String prodtype;






	public NewAccountList(String accid, String curr, String amount, String acctype,String prodtype) {
		this.accid = accid;
this.curr = curr;
        this.amount = amount;
        this.acctype = acctype;
        this.prodtype = prodtype;
	}



	public String getAccId() {
		return accid;
	}
	public void setAccId(String accid) {
		this.accid = accid;
	}

	public String getCurr() {
		return curr;
	}
	public void setCurr(String curr) {
		this.curr = curr;
	}

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
    }

    public String getProdtype() {
        return prodtype;
    }
    public void setProdtype(String acct) {
        this.prodtype = acct;
    }
	
	
}
