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

public class MicroLoanList {

	private String ldesc;

    private String acno;
    private String pen;
    private String lbal;
    private String intp;
    private String prncloan;
    private String disburs;





	public MicroLoanList(String ldesc,

                         String acno,
                         String pen,
                         String lbal,
                         String intp,
                         String princ,

                         String disburs) {

		this.ldesc = ldesc;

        this.acno = acno;
        this.pen = pen;

        this.lbal = lbal;
        this.intp = intp;
        this.prncloan = princ;
        this.disburs = disburs;
	}



	public String getLdesc() {
		return ldesc;
	}
	public void setLdesc(String accid) {
		this.ldesc = accid;
	}

	public String getLbal() {
		return lbal;
	}
	public void setLbal(String curr) {
		this.lbal = curr;
	}

    public String getAcno() {
        return acno;
    }
    public void setAcno(String amon) {
        this.acno = amon;
    }

    public String getIrate() {
        return intp;
    }
    public void setIrate(String acct) {
        this.intp = acct;
    }


    public String getPrinc() {
        return prncloan;
    }
    public void setPrinc(String curr) {
        this.prncloan = curr;
    }

    public String getPen() {
        return pen;
    }
    public void setPen(String amon) {
        this.pen = amon;
    }

    public String getDisburs() {
        return disburs;
    }
    public void setDisburs(String acct) {
        this.disburs = acct;
    }

	
}
