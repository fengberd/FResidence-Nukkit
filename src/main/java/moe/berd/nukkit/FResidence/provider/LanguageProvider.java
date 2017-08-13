package moe.berd.nukkit.FResidence.provider;

import cn.nukkit.lang.*;

import moe.berd.nukkit.FResidence.*;

public class LanguageProvider extends BaseLang
{
	public LanguageProvider(Main main,String lang,String fallback)
	{
		super(lang,null,fallback);
		this.lang=this.loadLang(main.getResource("lang/"+this.langName+".ini"));
		this.fallbackLang=this.loadLang(main.getResource("lang/"+fallback+".ini"));
	}
}
