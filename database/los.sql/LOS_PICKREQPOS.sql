SET DEFINE OFF;
Insert into JBOSS.LOS_PICKREQPOS
   (ID, CREATED, MODIFIED, VERSION, ENTITY_LOCK, AMOUNT, PICKEDAMOUNT, SOLVED, CANCELED, PICKINGSUPPLYTYPE, PICKINGDIMENSIONTYPE, WITHDRAWALTYPE, SUBSTITUTIONTYPE, POS_INDEX, PARENTREQUEST_ID, PICKREQUEST_ID, STOCKUNIT_ID)
 Values
   (2200, TO_TIMESTAMP('20/11/2012 9:38:43.957000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('20/11/2012 9:38:45.103000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 1, 0, 30, 30, 1, 0, 'STATIC_DECENTRALIZED', 'ONE_DIMENSIONAL', 'UNORDERED_FROM_STOCKUNIT', 'SUBSTITUTION_SAME_LOT', 0, 2001, 2002, 2350);
Insert into JBOSS.LOS_PICKREQPOS
   (ID, CREATED, MODIFIED, VERSION, ENTITY_LOCK, AMOUNT, PICKEDAMOUNT, SOLVED, CANCELED, PICKINGSUPPLYTYPE, PICKINGDIMENSIONTYPE, WITHDRAWALTYPE, SUBSTITUTIONTYPE, POS_INDEX, PARENTREQUEST_ID, PICKREQUEST_ID, STOCKUNIT_ID)
 Values
   (2201, TO_TIMESTAMP('20/11/2012 9:42:38.195000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('20/11/2012 9:42:38.291000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 1, 0, 25, 25, 1, 0, 'STATIC_DECENTRALIZED', 'ONE_DIMENSIONAL', 'UNORDERED_FROM_STOCKUNIT', 'SUBSTITUTION_SAME_LOT', 0, 2005, 2006, 2351);
Insert into JBOSS.LOS_PICKREQPOS
   (ID, CREATED, MODIFIED, VERSION, ENTITY_LOCK, AMOUNT, PICKEDAMOUNT, SOLVED, CANCELED, PICKINGSUPPLYTYPE, PICKINGDIMENSIONTYPE, WITHDRAWALTYPE, SUBSTITUTIONTYPE, POS_INDEX, PARENTREQUEST_ID, PICKREQUEST_ID, STOCKUNIT_ID)
 Values
   (3250, TO_TIMESTAMP('21/11/2012 8:02:01.564000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('21/11/2012 8:02:02.650000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 1, 0, 20, 20, 1, 0, 'STATIC_DECENTRALIZED', 'ONE_DIMENSIONAL', 'UNORDERED_FROM_STOCKUNIT', 'SUBSTITUTION_SAME_LOT', 0, 3151, 3152, 3400);
COMMIT;
