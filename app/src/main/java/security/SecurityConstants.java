package security;



public interface SecurityConstants {

	public String YEAR_FRMT = "MM/dd/yyyy H:mm:ss a";

	public String CELLULANT_YEAR_FRMT = "yyyy-MM-dd H:mm:ss";

	public String YEAR = "yyyy";

	public String GEN_MALE = "M";

	public String VAL_00 = "00";

	public String VAL_01 = "01";

	public String VAL_02 = "02";

	public String VAL_03 = "03";

	public String VAL_DOT_00 = ".00";

	public String VAL_DOT_01 = ".01";

	public String VAL_DOT_02 = ".02";

	public String VAL_DOT_03 = ".03";

	public String VAL_001 = "001";

	public String VAL_002 = "002";

	public String VAL_003 = "003";

	public String ACC_OPEN_PEN_FLAG = "FP";

	public String NAT3_ACC_OPEN_PEN_FLAG = "NP";

	public String ACC_OPEN_ID_FRMT = "yyddMMHmmssSSS";

	public String ACC_OPEN_CHANNEL_ID_MOBILE = "MOBBK";

	public String CONFIG_BUNDLE = "config";
	public String NARR_PATTERN_COMP = "\\$(.*?)\\$";
	public String NARR_DOLL_SYM = "\\$";
	public String BAL_INQ_REG_SYM = "(?<=\\G.{12})";
	public String MINI_STMT_REG_SYM = "(?<=\\G.{61})";
	public String MINI_SUCCESS_ZERO = "000000000000";
	public String MINI_AVAIL_BALANCE = "AVAIL BALANCE";
	public String BALANCE_FORMAT = "###,###.###";

	public String CONFIG_BUNDLE_ERROR = "resources/errors";
	public String RESOURCE = "/resources/";
	public String FUNDS_TRAN_DET = "transactiondetails.xml";
	public String NHIF_TRAN_DET = "nhiftransactiondetails.xml";
	public String FIMI_TRAN_CODES = "fimi_tran_codes";
	public String ISO_RESPONSE_CODES = "iso_response_codes";

	// The below is for switch constant codes.

	public char CHAR_VAL_0 = '0';
	public String VAL_0 = "0";
	public String VAL_139 = "139";
	public int INT_VAL_0 = 0;
	public int INT_VAL_1 = 1;
	public String GMT = "GMT";
	public String YES = "Y";
	public String NHIF_IDEN_CRD = "IC";
	public String NO = "N";

	public String TRAN_LOG_ID = "tranlogid";
	public String IMAL_TRAN_LOG_ID = "imaltranlogid";
	public String BFUB_TRAN_LOG_ID = "bfubtranlogid";
	public String TXN_ID = "txnid";
	public String MOBILE_NO = "mobileno";
	public String CURRENCY = "currency";
	public String TO_MOBILE_NO = "tomobileno";
	public String TXN_STAN = "txnstan";
	public String TXN_RRN = "txnrrn";
	public String REV_TXN_STAN = "revtxnstan";
	public String REV_TXN_RRN = "revtxnrrn";
	public String TXN_AMT = "txnamt";
	public String NARRATION = "narration";
	public String INT_TRN_CHK_CODE = "intertranscheckcode";
	public String INSTITUTE = "institute";
	public String ACCOUNT = "account";
	public String INPUT = "input";
	public String STATUS = "status";
	public String CARD_NUMBER = "cardnumber";
	public String NHIF_COMMON_NO = "commonno";
	public String SESS_ID = "sessionid";
	public String SUCESS_SMALL = "Success";
	public String SUCESS_CAPS = "SUCCESS";
	public String XML = "xml";
	public String ISO_REQ_XML = "isoreqxml";
	public String ISO_RES_XML = "isoresxml";
	public String NHIF_RESP_STR = "nhifresp";
	public String NHIF_RESP_CODE = "respcode";
	public String NHIF_RESP_MESSG = "respmessg";
	public String NHIF_MOB_EMAIL = "nhifmobemail";
	public String NHIF_SPACE = " ";
	public String TXN_CODE = "txncode";
	public String MESSAGE = "message";
	public String TXN_SUB_CODE = "txnsubcode";
	public String FND_TXN_CODE = "fundtxncode";

	public String TXN_PROC_CODE = "processingcode";
	public String TXN_NARRATION = "narration";
	public String TXN_FIN_STAT = "financialstatus";
	public String TXN_NON_FIN_STAT = "nonfinancialstatus";
	public String TXN_PREP_STAT = "preparestatus";
	public String FIMI_STATUS = "fimistatus";
	public String TXN_COMP_STAT = "completestatus";

	public String KEN_CURR = "KES";
	public String MOBILE = "mobile";
	public String SERVICE_ID = "serviceid";
	public String CELL_USER = "username";
	public String CELL_PASS = "password";
	public String CELL_FULL_NAME = "fullname";
	public String CELL_ACCT_NUMBER = "accountnumber";
	public String MEANS_OF_PAYMENT = "meansofpayment";
	public String BRANCH_CODE = "branchcode";

	// public String BAL_TXNCODE = "310000";
	// public String MINI_STMT_TXNCODE = "940000";
	// public String FUNDS_OWN_TRNF_TXNCODE = "710000";
	// public String FUNDS_WITH_BNK_TXNCODE = "720000";
	// public String FUNDS_TO_PAY_TXNCODE = "740000";
	// public String AIR_TO_OWN_MOB_TXNCODE = "750000";
	// public String AIR_TO_OTH_MOB_TXNCODE = "760000";
	// public String MPESA_TO_BNK_TXNCODE = "780000";
	// public String BNK_TO_MPESA_TXNCODE = "800000";
	// public String FUNDS_TO_MYBILL_TXNCODE = "810000";
	// public String FUNDS_TO_OTBILL_TXNCODE = "820000";

	public String VAL_404 = "404";
	public String AMOUNT = "amount";
	public String AMOUNT_TO_NHIF = "amounttonhif";
	public String DB_ACCT = "debitaccount";
	public String CR_ACCT = "creditaccount";
	public String REV_DB_ACCT = "revdebitaccount";
	public String REV_CR_ACCT = "revcreditaccount";
	public String NHIF_PROCESSING_CODE = "nhifproccode";
	public String NHIF_TYPE_OF_SEARCH = "typeofsearch";

	public String MTI_HEADER = "0200";
	public String NAC_TPDU = "ISO036000005";
	public String ASCII_TPDU = "ISO006000040";
	public String STATIC_CARD = "4459710000000000";
	public String REV_FLD_12 = "revfld12";
	public String REV_FLD_13 = "revfld13";
	public String REV_FLD_37 = "revfld37";
	public String REV_FLAG = "revflag";

	public String REV_MTI_HEAD = "0420";
	public String POS_1_4 = "0200";
	public String POS_33_42 = "0000000000";

	public String FLD_11 = "MI0001";
	public String FLD_28 = "00000000";
	public String FLD_32 = "445971";
	public String FLD_35 = "379999999999999999=99999999999999999999";
	public String FLD_41 = "MOBEE           ";
	public String FLD_43 = "NBK                   NAIROBI         KE";
	public String FLD_61 = "0130001NBOK0000";

	public String ISO_CHANNEL = "iso-channel";
	public String OUT_LOG = "outLog";
	public String IMAL_SWITCH_IP = "imal.switch.ip";
	public String IMAL_SWITCH_PORT = "imal.switch.port";
	public String IMAL_SWITCH_TIME_OUT = "imal.switch.timeout";

	public String BFUB_SWITCH_IP = "bfub.switch.ip";
	public String BFUB_SWITCH_PORT = "bfub.switch.port";
	public String BFUB_SWITCH_TIME_OUT = "bfub.switch.timeout";

	public String BFUB = "BFUB";
	public String IMAL = "IMAL";

	public String REF_ID = "referenceid";
	public String RESP_CODE = "responsecode";
	public String RESP_MESSAGE = "responsemessage";
	public String RESP_BALANCE = "balance";

	public String RESP_ACCOUNTS = "accounts";
	public String RESP_CUSTOMER = "customer";

	public String RESP_BRANCHES = "branches";
	public String RESP_BRANCH_CODE = "branchcode";
	public String RESP_BRANCH_NAME = "branchname";
	public String ERROR_CODE = "errorcode";
	public String ERROR_MESSAGE = "errormessage";

	public int NHIF_PAY_METHOD = 2;
	
	
	public String MASTER_KEY=null;
	public String KEYGEN_ALG = "AES";
	public int SYMETRIC_KEY_SIZE = 256;
	public String PKI_PROVIDER="BC";
	public String SYMETRICKEY_ALG = "AES/CBC/PKCS7Padding"; 
	
	public String KEYFACTORY_ALG = "RSA";

}
