package edu.jhuapl.sbmt.core.listeners;

import edu.jhuapl.saavtk.model.IPositionOrientationManager;
import edu.jhuapl.sbmt.core.body.SmallBodyModel;

public interface PositionOrientationManagerListener
{
	public void managerUpdated(IPositionOrientationManager<SmallBodyModel> manager);
}
