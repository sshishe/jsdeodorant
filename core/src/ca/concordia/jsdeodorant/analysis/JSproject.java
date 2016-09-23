package ca.concordia.jsdeodorant.analysis;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.jscomp.SourceFile;

import ca.concordia.jsdeodorant.analysis.abstraction.Module;
import ca.concordia.jsdeodorant.analysis.abstraction.Program;
import ca.concordia.jsdeodorant.analysis.abstraction.SourceElement;
import ca.concordia.jsdeodorant.analysis.decomposition.Statement;
import ca.concordia.jsdeodorant.analysis.module.PackageExporter;
import ca.concordia.jsdeodorant.analysis.module.PackageImporter;
import ca.concordia.jsdeodorant.analysis.module.PackageSystem;
import ca.concordia.jsdeodorant.analysis.module.closurelibrary.ClosureLibraryExportHelper;
import ca.concordia.jsdeodorant.analysis.module.closurelibrary.ClosureLibraryImportHelper;
import ca.concordia.jsdeodorant.analysis.module.commonjs.CommonJSExportHelper;
import ca.concordia.jsdeodorant.analysis.module.commonjs.CommonJSRequireHelper;
import ca.concordia.jsdeodorant.analysis.module.helma.HelmaRequireHelper;

public class JSproject {
	
	private List<Module> modules;
	private static JSproject instance=null;
	
	public static JSproject getInstance(){
		if(instance==null){
			instance= new JSproject();
		}
		return instance;
	}
	
	private JSproject(){
		this.modules= new ArrayList<Module>();
	}

	public List<Module> getModules() {
		return modules;
	}
	
	public void createModule(Program program,SourceFile sourceFile, List<String> messages, PackageSystem packageSystem , boolean hasModuleAnalysis){
		Module module = new Module(program, sourceFile, messages);
		this.modules.add(module);
		if(hasModuleAnalysis)
			this.processModules(module, packageSystem,true);
		
	}
	
	public void processModules(Module module, PackageSystem packageSystem, boolean onlyExports) {
		PackageImporter packageImporter = null;
		PackageExporter packageExporter = null;
		switch (packageSystem) {
		case CommonJS:
			if (!onlyExports)
				packageImporter = new CommonJSRequireHelper(module, modules);
			packageExporter = new CommonJSExportHelper(module, modules);
			break;
		case ClosureLibrary:
			if (!onlyExports)
				packageImporter = new ClosureLibraryImportHelper(module, modules);
			packageExporter = new ClosureLibraryExportHelper(module, modules);
			break;
		case Helma:
			if (!onlyExports)
				packageImporter = new HelmaRequireHelper(module, modules);
			break;
		default:
			break;
		}

		Program program = module.getProgram();
		for (SourceElement element : program.getSourceElements()) {
			if (element instanceof Statement) {
				Statement statement = (Statement) element;
				if (!onlyExports)
					packageImporter.extract(statement.getStatement());
				if(packageExporter !=null)
					packageExporter.extract(statement.getStatement());
			}
		}
	}


}
