package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 合同完成信息(合同信息中一个按钮输入，)
 */
@Entity
@Table(name = "w_contract_finish_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractFinshInfo")
public class ContractFinshInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 合同主键
     */
    @Column(name = "contract_id")
    private Long contractId;
    /**
     * 完成率，实时更新到合同信息中，单位%，精确到小数点后2位，如80.05%就填写80.05
     */
    @Column(name = "finish_rate")
    private Double finishRate;

    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Double getFinishRate() {
		return finishRate;
	}

	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractFinshInfo contractReceive = (ContractFinshInfo) o;
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
		return "ContractFinshInfo [id=" + id + ", contractId=" + contractId + ", finishRate=" + finishRate
				+ ", creator=" + creator + ", createTime=" + createTime + "]";
	}
}
