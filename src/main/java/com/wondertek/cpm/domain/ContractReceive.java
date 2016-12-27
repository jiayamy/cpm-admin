package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同回款信息
 */
@Entity
@Table(name = "w_contract_receive")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractreceive")
public class ContractReceive implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "contract_id")
    private Long contractId;			//	合同主键 

    @Column(name = "receive_total")
    private Double receiveTotal;		//	收款额---实时更新合同信息中的收款金额（根据状态为可用或删除来操作）

    @Column(name = "receive_day")
    private Long receiveDay;			//	收款时间(格式：20161227)
    
    @Column(name = "receiver_")
    private String receiver;			//		收款人

    @Column(name = "status_")
    private Integer status;				//	状态（可用，删除）

    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public ContractReceive contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Double getReceiveTotal() {
        return receiveTotal;
    }

    public ContractReceive receiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
        return this;
    }

    public void setReceiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
    }

    public Long getReceiveDay() {
        return receiveDay;
    }

    public ContractReceive receiveDay(Long receiveDay) {
        this.receiveDay = receiveDay;
        return this;
    }

    public void setReceiveDay(Long receiveDay) {
        this.receiveDay = receiveDay;
    }

    public Integer getStatus() {
        return status;
    }

    public ContractReceive status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public ContractReceive creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractReceive createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ContractReceive updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ContractReceive updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getReceiver() {
        return receiver;
    }

    public ContractReceive receiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractReceive contractReceive = (ContractReceive) o;
        if (contractReceive.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, contractReceive.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ContractReceive{" +
            "id=" + id +
            ", contractId='" + contractId + "'" +
            ", receiveTotal='" + receiveTotal + "'" +
            ", receiveDay='" + receiveDay + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            ", receiver='" + receiver + "'" +
            '}';
    }
}
