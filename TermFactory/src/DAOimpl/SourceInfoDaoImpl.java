package DAOimpl;

import java.util.ArrayList;
import java.util.List;

import DAO.SourceInfoDao;
import model.SourceInfo;

public class SourceInfoDaoImpl extends commonDaoImpl implements SourceInfoDao{
	public List<SourceInfo> getBioPortalSourceInfo(String CollectionName, SourceInfo queryString){
		List<SourceInfo> result = new ArrayList<SourceInfo>();
		return result;
	}
}
