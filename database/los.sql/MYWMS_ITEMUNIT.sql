SET DEFINE OFF;
Insert into JBOSS.MYWMS_ITEMUNIT
   (ID, CREATED, MODIFIED, VERSION, ENTITY_LOCK, UNITTYPE, UNITNAME, BASEFACTOR)
 Values
   (52, TO_TIMESTAMP('20/11/2012 8:51:14.564000 ��','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('20/11/2012 8:51:14.564000 ��','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 0, 0, 'PIECE', 'Lt', 1);
Insert into JBOSS.MYWMS_ITEMUNIT
   (ID, CREATED, MODIFIED, ADDITIONALCONTENT, VERSION, ENTITY_LOCK, UNITTYPE, UNITNAME, BASEFACTOR)
 Values
   (0, TO_TIMESTAMP('20/11/2012 8:52:40.555000 ��','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('20/11/2012 8:52:40.558000 ��','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it.', 0, 0, 'PIECE', 'Pce', 1);
COMMIT;
