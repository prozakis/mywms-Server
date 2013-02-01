package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
import org.mywms.model.VehicleData;

@Local
public interface VehicleDataService
    extends BasicService<VehicleData>
{
	public VehicleData create();

	public VehicleData getByPlateNumber(String plateNumber) throws EntityNotFoundException;
	public VehicleData getByLabelId(String labelId) throws EntityNotFoundException;
	public String getPlateNumberByLabelId(String labelId) throws EntityNotFoundException;
}
