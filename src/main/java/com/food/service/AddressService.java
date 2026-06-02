// 技术栈：Spring Boot + MyBatis
package com.food.service;

import com.food.common.Result;
import com.food.entity.Address;
import com.food.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 添加地址，若设为默认，先清除用户原有默认地址
     */
    @Transactional
    public Result addAddress(Long userId, String receiverName, String receiverPhone,
                             String province, String city, String district,
                             String detail, BigDecimal latitude, BigDecimal longitude,
                             Integer isDefault) {
        Address addr = new Address();
        addr.setUserId(userId);
        addr.setReceiverName(receiverName);
        addr.setReceiverPhone(receiverPhone);
        addr.setProvince(province);
        addr.setCity(city);
        addr.setDistrict(district);
        addr.setDetail(detail);
        addr.setLatitude(latitude);
        addr.setLongitude(longitude);
        addr.setIsDefault(isDefault != null && isDefault == 1 ? 1 : 0);

        if (addr.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
        }
        addressMapper.insert(addr);
        return Result.success(addr);
    }

    /**
     * 更新地址，同样处理默认逻辑
     */
    @Transactional
    public Result updateAddress(Long userId, Long addressId, String receiverName,
                                String receiverPhone, String province, String city,
                                String district, String detail, BigDecimal latitude,
                                BigDecimal longitude, Integer isDefault) {
        Address existing = addressMapper.selectById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作该地址");
        }

        existing.setReceiverName(receiverName);
        existing.setReceiverPhone(receiverPhone);
        existing.setProvince(province);
        existing.setCity(city);
        existing.setDistrict(district);
        existing.setDetail(detail);
        existing.setLatitude(latitude);
        existing.setLongitude(longitude);
        existing.setIsDefault(isDefault != null && isDefault == 1 ? 1 : 0);

        if (existing.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
        }
        addressMapper.updateById(existing);
        return Result.success("更新成功");
    }

    /**
     * 删除地址
     */
    public Result deleteAddress(Long userId, Long addressId) {
        Address existing = addressMapper.selectById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作该地址");
        }
        addressMapper.deleteById(addressId);
        return Result.success("删除成功");
    }

    /**
     * 获取用户所有地址
     */
    public Result listAddresses(Long userId) {
        List<Address> list = addressMapper.selectByUserId(userId);
        return Result.success(list);
    }

    /**
     * 获取默认地址
     */
    public Result getDefaultAddress(Long userId) {
        List<Address> list = addressMapper.selectByUserId(userId);
        Address defaultAddr = list.stream()
                .filter(a -> a.getIsDefault() == 1)
                .findFirst()
                .orElse(null);
        return Result.success(defaultAddr); // 可能为空，但 Result.success 会包装空对象
    }
}
