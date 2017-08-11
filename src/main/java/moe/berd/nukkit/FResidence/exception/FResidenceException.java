package moe.berd.nukkit.FResidence.exception;

import moe.berd.nukkit.FResidence.utils.*;

public class FResidenceException extends Exception
{
	public FResidenceException(String message)
	{
		super(Utils.translate(message));
	}
}
