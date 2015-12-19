package DAOimpl;

import java.util.ArrayList;
import java.util.List;

import DAO.bioPortalSourceDao;
import model.bioPortalSource;

public class bioPortalSourceDaoImpl extends commonDaoImpl implements bioPortalSourceDao{
	
	public List<bioPortalSource> getBioPortalSourceInfo(String CollectionName, bioPortalSource queryString){
		List<bioPortalSource> result = new ArrayList<bioPortalSource>();
		return result;
	}
}
