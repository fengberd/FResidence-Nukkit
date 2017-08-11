package moe.berd.nukkit.FResidence.utils;

public class ConfirmInfo
{
	public long expires;
	public String action;
	public String[] args;
	
	public ConfirmInfo(String action,String[] args,int expiresSeconds)
	{
		this.args=args;
		this.action=action;
		this.expires=System.currentTimeMillis()+expiresSeconds*1000;
	}
	
	public boolean expired()
	{
		return expires<System.currentTimeMillis();
	}
}
