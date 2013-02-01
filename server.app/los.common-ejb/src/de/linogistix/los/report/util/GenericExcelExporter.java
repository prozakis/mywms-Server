/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.log4j.Logger;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.TypeResolver;

public class GenericExcelExporter {

	private static final Logger log = Logger
			.getLogger(GenericExcelExporter.class);

	@SuppressWarnings("unchecked")
	public byte[] export(String title, List<BODTO> exportList) {

		return export(title, exportList, null);

	}

	@SuppressWarnings("unchecked")
	public byte[] export(String title, List<BODTO> exportList,
			List<PropertyDescriptor> props) {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		if (exportList == null || exportList.size() == 0) {
			return new byte[0];
		}

		try {
			Object bean = exportList.get(0);
			BeanInfo infoTo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] d = infoTo.getPropertyDescriptors();

			if (props == null || props.isEmpty()) {

				props = new ArrayList<PropertyDescriptor>();

				for (int i = 0; i < d.length; i++) {
					try {
						if (d[i].getPropertyType()
								.isAssignableFrom(Class.class)) {
							continue;
						}
						if (d[i].getName().equals("className")) {
							continue;
						}

						if (d[i].getName().equals("className")
								|| d[i].getName().equals("id")
								|| d[i].getName().equals("version")) {
							continue;
						}

						props.add(d[i]);
					} catch (Exception ex) {
						log.error(ex);
						continue;
					}

				}
			}

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("ReportTitle", title);

			JasperDesign design = getJasperDesign(props);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(design);

			JasperPrint jasperPrint = null;

			jasperPrint = JasperFillManager.fillReport(jasperReport,
					parameters, new JRBeanCollectionDataSource(exportList));

			JRXlsExporter xlsExporter = new JRXlsExporter();
			xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT,
					jasperPrint);
			xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			xlsExporter
					.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
							Boolean.FALSE);
			xlsExporter.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.TRUE);
			xlsExporter.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);

			// writeHeader(response, exportName);

			xlsExporter.exportReport();
			
			byte[] ret = out.toByteArray();
			
			out.close();
			
			return ret;
			
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private static JasperDesign getJasperDesign(List<PropertyDescriptor> props)
			throws JRException {
		// JasperDesign
		JasperDesign jasperDesign = new JasperDesign();
		jasperDesign.setName("NoXmlDesignReport");
		jasperDesign.setPageWidth(595);
		jasperDesign.setPageHeight(842);
		jasperDesign.setColumnWidth(515);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setColumnCount(1);
		jasperDesign.setLeftMargin(40);
		jasperDesign.setRightMargin(40);
		jasperDesign.setTopMargin(50);
		jasperDesign.setBottomMargin(50);

		// Fonts
		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName("Sans_Normal");
		normalStyle.setDefault(true);
		normalStyle.setFontName("DejaVu Sans");
		normalStyle.setFontSize(12);
		normalStyle.setPdfFontName("Helvetica");
		normalStyle.setPdfEncoding("Cp1252");
		normalStyle.setPdfEmbedded(false);
		jasperDesign.addStyle(normalStyle);

		JRDesignStyle boldStyle = new JRDesignStyle();
		boldStyle.setName("Sans_Bold");
		boldStyle.setFontName("DejaVu Sans");
		boldStyle.setFontSize(12);
		boldStyle.setBold(true);
		boldStyle.setPdfFontName("Helvetica-Bold");
		boldStyle.setPdfEncoding("Cp1252");
		boldStyle.setPdfEmbedded(false);
		jasperDesign.addStyle(boldStyle);

		JRDesignStyle italicStyle = new JRDesignStyle();
		italicStyle.setName("Sans_Italic");
		italicStyle.setFontName("DejaVu Sans");
		italicStyle.setFontSize(12);
		italicStyle.setItalic(true);
		italicStyle.setPdfFontName("Helvetica-Oblique");
		italicStyle.setPdfEncoding("Cp1252");
		italicStyle.setPdfEmbedded(false);
		jasperDesign.addStyle(italicStyle);

		// Parameters
		JRDesignParameter parameter = new JRDesignParameter();
		parameter.setName("ReportTitle");
		parameter.setValueClass(java.lang.String.class);
		jasperDesign.addParameter(parameter);

		// Fields
		int i = 1;
		for (PropertyDescriptor p : props) {
			JRDesignField field = new JRDesignField();
			field.setName(p.getName());
			field.setValueClass(resolveType(p));
			jasperDesign.addField(field);
			i++;
		}

		// Title
		JRDesignBand band = new JRDesignBand();
		band.setHeight(50);

		JRDesignTextField textField = new JRDesignTextField();
		textField.setBlankWhenNull(true);
		textField.setX(0);
		textField.setY(20);
		textField.setWidth(510);
		textField.setHeight(30);
		textField.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
		textField.setStyle(normalStyle);
		textField.setFontSize(22);
		JRDesignExpression expression = new JRDesignExpression();
		expression.setValueClass(java.lang.String.class);
		expression.setText("$P{ReportTitle}");
		textField.setExpression(expression);
		band.addElement(textField);
		jasperDesign.setTitle(band);

		// Page header
		band = new JRDesignBand();
		band.setHeight(20);
		int x = 0;
		for (PropertyDescriptor p : props) {
			JRDesignStaticText staticText = new JRDesignStaticText();
			staticText.setX(x);
			staticText.setY(0);
			staticText.setWidth(55);
			staticText.setHeight(20);
			staticText
					.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
			staticText.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
			staticText.setStyle(boldStyle);
			staticText
					.setStretchType(JRDesignStaticText.STRETCH_TYPE_RELATIVE_TO_TALLEST_OBJECT);
			staticText.setText(p.getDisplayName());
			band.addElement(staticText);
			x += 55;
		}

		jasperDesign.setPageHeader(band);

		// Column header
		band = new JRDesignBand();
		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setHeight(25);
		x = 0;
		for (PropertyDescriptor p : props) {
			textField = new JRDesignTextField();
			textField.setX(x);
			textField.setY(0);
			textField.setWidth(55);
			textField.setHeight(20);
			textField
					.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
			textField.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
			textField.setStyle(normalStyle);
			expression = new JRDesignExpression();
			Class type = resolveType(p);
			expression.setValueClass(type);
			expression.setText("$F{" + p.getName() + "}");
			textField.setExpression(expression);
			textField.setStretchWithOverflow(true);
			band.addElement(textField);
			x += 55;
		}

		jasperDesign.setDetail(band);

		// Column footer
		band = new JRDesignBand();
		jasperDesign.setColumnFooter(band);

		// Page footer
		band = new JRDesignBand();
		jasperDesign.setPageFooter(band);

		// Summary
		band = new JRDesignBand();
		jasperDesign.setSummary(band);

		return jasperDesign;
	}

	@SuppressWarnings("unchecked")
	private static Class resolveType(PropertyDescriptor d) {
		if (TypeResolver.isIntegerType(d.getPropertyType())) {
			return Integer.class;
		} else if (TypeResolver.isLongType(d.getPropertyType())) {
			return Long.class;
		} else {
			return d.getPropertyType();
		}
	}

}
