package test;

import java.util.Iterator;
import java.util.List;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;

import com.eryansky.common.utils.collections.Collections3;

/**
 *   测试.
 */
public class Test {

	public static void main(String[] args) {


        List<String> list1 = Lists.newArrayList();
        List<String> list2 = Lists.newArrayList();
        list1.add("01");
        list1.add("00");
        list1.add("0011");
        int min = 0;
        Iterator<String> iterator = list1.iterator();
        while (iterator.hasNext()){
            String str = iterator.next();
            System.out.println(str);
            if(min==0){
                min = str.length();
                list2.add(str);
            }else if(str.length() <=min){
                list2.add(str);
            }

        }
        System.out.println(JsonMapper.getInstance().toJson(list2));



//        System.out.println(new NullPointerException("空指针一次").getClass().getSimpleName());
//        System.out.println(new NullPointerException("空指针一次").getClass().getName());
//        List<Long> ids = null;
//		if (!Collections3.isEmpty(ids)) {
//			for (Long id : ids) {
//				System.out.println(id);
//			}
//		}
//
//
//		try {
//			Validate.notBlank("", "queryString不能为空");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
		
		
	}

}
