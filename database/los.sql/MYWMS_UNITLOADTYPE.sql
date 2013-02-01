SET DEFINE OFF;
Insert into JBOSS.MYWMS_UNITLOADTYPE
   (ID, CREATED, MODIFIED, ADDITIONALCONTENT, VERSION, ENTITY_LOCK, NAME)
 Values
   (1, TO_TIMESTAMP('20/11/2012 9:38:43.937000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('20/11/2012 9:38:43.941000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 'Unit load type for pickinglocations.

This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it.', 1, 0, 'Picking');
Insert into JBOSS.MYWMS_UNITLOADTYPE
   (ID, CREATED, MODIFIED, ADDITIONALCONTENT, VERSION, ENTITY_LOCK, NAME)
 Values
   (0, TO_TIMESTAMP('20/11/2012 9:18:25.468000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), TO_TIMESTAMP('20/11/2012 9:18:25.496000 面','fmDDfm/fmMMfm/YYYY fmHH12fm:MI:SS.FF AM'), 'Defaultvalue for unit load types.

This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it.', 1, 0, 'Default');
COMMIT;
