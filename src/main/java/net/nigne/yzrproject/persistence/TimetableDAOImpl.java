package net.nigne.yzrproject.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import net.nigne.yzrproject.domain.GenreVO;
import net.nigne.yzrproject.domain.MovieVO;
import net.nigne.yzrproject.domain.PlexVO;
import net.nigne.yzrproject.domain.ReservationVO;
import net.nigne.yzrproject.domain.TheaterVO;
import net.nigne.yzrproject.domain.TimetableVO;

@Repository
public class TimetableDAOImpl implements TimetableDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Map<String, Object> getTimetable(String theater_id, String day) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TimetableVO> root = cq.from(TimetableVO.class);
		
		cq.select(root.get("movie_id"));
		cq.distinct(true);
		cq.where(cb.and(cb.equal(root.get("theater_id"), theater_id), cb.like(root.get("start_time"), "%"+day+"%")));
		
		TypedQuery<String> tq = entityManager.createQuery(cq);
		List<String> tt_movie_id = tq.getResultList();
		
		Predicate p = cb.equal(root.get("theater_id"), theater_id);
		Predicate p2 = cb.like(root.get("start_time"), "%"+day+"%");
		
		CriteriaQuery<TimetableVO> ttcq = cb.createQuery(TimetableVO.class);
		Root<TimetableVO> ttroot = ttcq.from(TimetableVO.class);
		TypedQuery<TimetableVO> ttq = null;
		Map<String, Object> ttmap = new HashMap<>();
		List<TimetableVO> tt = null;
		
		String m_name = "";
		for(int i=0; i<tt_movie_id.size(); i++){
			ttcq.where(cb.and(p, p2, cb.equal(ttroot.get("movie_id"), tt_movie_id.get(i))));
			ttq = entityManager.createQuery(ttcq);
			tt = ttq.getResultList();
			m_name = Integer.toString(i);
			ttmap.put(m_name, tt);
		}
		
		return ttmap;
	}

	@Override
	public List<MovieVO> getMovieInfo(String theater_id, String day) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TimetableVO> root = cq.from(TimetableVO.class);
		
		cq.select(root.get("movie_id"));
		cq.distinct(true);
		cq.where(cb.and(cb.equal(root.get("theater_id"), theater_id), cb.like(root.get("start_time"), "%"+day+"%")));
		
		TypedQuery<String> tq = entityManager.createQuery(cq);
		List<String> tt_movie_id = tq.getResultList();
		
		CriteriaQuery<MovieVO> mcq = cb.createQuery(MovieVO.class);
		Root<MovieVO> mroot = mcq.from(MovieVO.class);
		TypedQuery<MovieVO> mtq = null;
		List<MovieVO> mlist = new ArrayList<MovieVO>();
		
		for(int i=0; i<tt_movie_id.size(); i++){
			mcq.where(cb.equal(mroot.get("movie_id"), tt_movie_id.get(i)));
			mtq = entityManager.createQuery(mcq);
			mlist.add(mtq.getSingleResult());
		}
		
		return mlist;
	}

	@Override
	public List<GenreVO> getMovieGenre(String theater_id, String day) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TimetableVO> root = cq.from(TimetableVO.class);
		
		cq.select(root.get("movie_id"));
		cq.distinct(true);
		cq.where(cb.and(cb.equal(root.get("theater_id"), theater_id), cb.like(root.get("start_time"), "%"+day+"%")));
		
		TypedQuery<String> tq = entityManager.createQuery(cq);
		List<String> tt_movie_id = tq.getResultList();
		
		CriteriaQuery<GenreVO> gcq = cb.createQuery(GenreVO.class);
		Root<GenreVO> groot = gcq.from(GenreVO.class);
		TypedQuery<GenreVO> gtq = null;
		List<GenreVO> glist = new ArrayList<GenreVO>();
		
		for(int i=0; i<tt_movie_id.size(); i++){
			gcq.where(cb.equal(groot.get("movie_id"), tt_movie_id.get(i)));
			gtq = entityManager.createQuery(gcq);
			glist.add(gtq.getSingleResult());
		}
		
		return glist;
	}

	@Override
	public List<PlexVO> getPlexInfo(String theater_id, String day) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TimetableVO> cq = cb.createQuery(TimetableVO.class);
		Root<TimetableVO> root = cq.from(TimetableVO.class);
		
		cq.multiselect(root.get("theater_id"), root.get("plex_number"));
		cq.distinct(true);
		cq.where(cb.and(cb.equal(root.get("theater_id"), theater_id), cb.like(root.get("start_time"), "%"+day+"%")));
		cq.orderBy(cb.asc(root.get("plex_number")));
		
		TypedQuery<TimetableVO> tq = entityManager.createQuery(cq);
		List<TimetableVO> tplist = tq.getResultList();
		
		CriteriaQuery<PlexVO> pcq = cb.createQuery(PlexVO.class);
		Root<PlexVO> proot = pcq.from(PlexVO.class);
		TypedQuery<PlexVO> ptq = null;
		List<PlexVO> plist = new ArrayList<PlexVO>();
		
		for(int i=0; i<tplist.size(); i++){
			pcq.where(cb.and(cb.equal(proot.get("theater_id"), tplist.get(i).getTheater_id()), cb.equal(proot.get("plex_number"), tplist.get(i).getPlex_number())));
			ptq = entityManager.createQuery(pcq);
			plist.add(ptq.getSingleResult());
		}
		
		return plist;
	}

	@Override
	public List<TheaterVO> getLike_theater(String member_id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TheaterVO> cq = cb.createQuery(TheaterVO.class);
		Root<TheaterVO> root = cq.from(TheaterVO.class);
		
		Subquery<ReservationVO> subQuery = cq.subquery(ReservationVO.class);
		Root<ReservationVO> subRoot = subQuery.from(ReservationVO.class);
		
		subQuery.select(subRoot.get("theater_id"));
		subQuery.where(cb.equal(subRoot.get("member_id"), member_id));
		
		cq.multiselect(root.get("theater_id"), root.get("theater_name"));
		cq.where(root.get("theater_id").in(subQuery));
		cq.groupBy(root.get("theater_id"), root.get("theater_name"));
		cq.orderBy(cb.desc(cb.countDistinct(root.get("theater_id"))));
		
		TypedQuery<TheaterVO> tq = entityManager.createQuery(cq);
		List<TheaterVO> list = tq.getResultList();
		
		return list;
	}
	
	

}
