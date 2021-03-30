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

public class TxnList {

	private String txnname;
    private String txamount;
    private int img;






	public TxnList(String txn, String txa,int img) {
		this.txnname = txn;
this.txamount = txa;
        this.img = img;

	}



	public String getTxnname() {
		return txnname;
	}
	public void setTxnname(String bname) {
		this.txnname = bname;
	}

	public String getTxamount() {
		return txamount;
	}
	public void setTxamount(String bmob) {
		this.txamount = bmob;
	}


    public int getImg() {
        return img;
    }
    public void setImg(int bmob) {
        this.img = bmob;
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
