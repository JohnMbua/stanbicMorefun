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

public class NewBenList {

	private String benname;
    private String benmob;

    private String benid;
    private String bentype;
    private String bank;
    private String accid;
    private String branch;




	public NewBenList(String benname, String benmob,String benid,String accid,String bentype,String bank,String branch) {
		this.benname = benname;
this.benmob = benmob;
        this.benid = benid;
        this.accid =accid;
        this.branch = branch;
        this.bentype = bentype;
        this.bank = bank;

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

    public String getBenid() {
        return benid;
    }
    public void setBenid(String bmob) {
        this.benid = bmob;
    }

    public String getBenType() {
        return bentype;
    }
    public void setBentype(String bname) {
        this.bentype = bname;
    }

    public String getBranch() {
        return branch;
    }
    public void setBranch(String bmob) {
        this.branch = bmob;
    }

    public String getBank() {
        return bank;
    }
    public void setBank(String bmob) {
        this.bank = bmob;
    }
    public String getAcc() {
        return accid;
    }
    public void setAcc(String bmob) {
        this.accid = bmob;
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
