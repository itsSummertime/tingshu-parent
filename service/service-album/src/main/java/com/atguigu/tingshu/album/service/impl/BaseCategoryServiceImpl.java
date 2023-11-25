package com.atguigu.tingshu.album.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.mapper.*;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.model.album.BaseAttribute;
import com.atguigu.tingshu.model.album.BaseCategory1;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategoryService {

	@Autowired
	private BaseCategory1Mapper baseCategory1Mapper;

	@Autowired
	private BaseCategory2Mapper baseCategory2Mapper;

	@Autowired
	private BaseCategory3Mapper baseCategory3Mapper;

	@Autowired
	private BaseCategoryViewMapper baseCategoryViewMapper;

	@Autowired
	private BaseAttributeMapper baseAttributeMapper;


	@Override
	public List<JSONObject> getCategoryList() {
		//	创建集合
		List<JSONObject> list = new ArrayList<>();
		//	组装数据：包含一级分类，二级分类，三级分类。 在三张表中.
		//	先获取到所有的一级分类数据
		List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(null);
		//	使用一级分类Id 进行分组，组成map 集合 key=category1Id value = List<BaseCategoryView>
		//	function 函数式接口：复制小括号{一个参数，括号可有可无}，写死右箭头，落地大括号
		//	Map<Long, List<BaseCategoryView>> listMap = baseCategoryViewList.stream().collect(Collectors.groupingBy(baseCategoryView -> baseCategoryView.getCategory1Id()));
		//	方法引用
		Map<Long, List<BaseCategoryView>> baseCategory1IdMap = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
		//	循环遍历
		Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator = baseCategory1IdMap.entrySet().iterator();
		while (iterator.hasNext()){
			//	获取map 集合中的数据
			Map.Entry<Long, List<BaseCategoryView>> entry = iterator.next();
			//	获取key
			Long category1Id = entry.getKey();
			//	获取value
			List<BaseCategoryView> baseCategoryViewList1 = entry.getValue();
			//	创建一级分类对象
			JSONObject category1 = new JSONObject();
			category1.put("categoryId",category1Id);
			category1.put("categoryName",baseCategoryViewList1.get(0).getCategory1Name());
			//	获取二级分类数据--必须在一级分类基础上。 key = category2Id  value = List<BaseCategoryView>
			Map<Long, List<BaseCategoryView>> baseCategory2IdMap = baseCategoryViewList1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
			//	循环遍历.
			Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator1 = baseCategory2IdMap.entrySet().iterator();
			//	创建一个集合来存储所有二级分类集合数据
			List<JSONObject> categoryChild2List = new ArrayList<>();
			while (iterator1.hasNext()){
				//	获取集合中entry对象
				Map.Entry<Long, List<BaseCategoryView>> entry1 = iterator1.next();
				//	获取key
				Long category2Id = entry1.getKey();
				//	获取二级分类Id 对应的集合数据
				List<BaseCategoryView> baseCategoryViewList2 = entry1.getValue();
				//	创建二级分类对象
				JSONObject category2 = new JSONObject();
				category2.put("categoryId",category2Id);
				category2.put("categoryName",baseCategoryViewList2.get(0).getCategory2Name());
				//	获取三级分类数据 所有的数据泛型都是集合的泛型.
				List<JSONObject> categoryChild3List = baseCategoryViewList2.stream().map(baseCategoryView -> {
					//	创建二级分类对象
					JSONObject category3 = new JSONObject();
					category3.put("categoryId", baseCategoryView.getCategory3Id());
					category3.put("categoryName", baseCategoryView.getCategory3Name());
					return category3;
				}).collect(Collectors.toList());
				//	将三级分类数据添加到二级分类中.
				category2.put("categoryChild",categoryChild3List);
				//	添加二级分类数据到集合
				categoryChild2List.add(category2);
			}
			//	将二级分类数据添加到一级分类中
			category1.put("categoryChild",categoryChild2List);
			//	将每个一级 分类对象添加到集合中
			list.add(category1);
		}
		//	返回数据
		return list;
	}

	@Override
	public List<BaseAttribute> findAttribute(Long category1Id) {
		//多表关联查询;
		return baseAttributeMapper.selectAttribute(category1Id);
	}
}
