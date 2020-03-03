package com.cetc.security.ddos.controller.adapter.odl;

import java.util.ArrayList;
import java.util.List;

import com.cetc.security.ddos.controller.adapter.FlowConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InstructionConfig {
	
	List<Instruction> instruction;
	
	public List<Instruction> getInstruction() {
		return instruction;
	}

	public void setInstruction(List<Instruction> instruction) {
		this.instruction = instruction;
	}
	
	public void createInstruction(FlowConfig odlFlow)
	{
		instruction = new ArrayList<Instruction>();
		Instruction instr = new Instruction();
		instr.setOrder(odlFlow.getInstrOrder());
		instr.createApplyAction(odlFlow);
		
		instruction.add(instr);
	}

	public static final class Instruction
	{
	
		private int order;
		
		@JsonProperty("apply-actions")
		public ApplyActions applyActions;
		
		@JsonProperty("meter")
		public MeterCase meterCase;
		
		
		public MeterCase getMeterCase() {
			return meterCase;
		}

		public void setMeterCase(MeterCase meterCase) {
			this.meterCase = meterCase;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public ApplyActions getApplyActions() {
			return applyActions;
		}

		public void setApplyActions(ApplyActions applyActions) {
			this.applyActions = applyActions;
		}
		
		public void createMeter(int meterId)
		{
			meterCase = new MeterCase();
			meterCase.setMeterId(meterId);
		}
		
		public void createApplyAction(FlowConfig odlFlow)
		{
			applyActions = new ApplyActions();
			
			applyActions.createAction(odlFlow);
			
		}
		
		public static final class MeterCase
		{
			@JsonProperty("meter-id")
			public int meterId;

			public int getMeterId() {
				return meterId;
			}

			public void setMeterId(int meterId) {
				this.meterId = meterId;
			}
			
		}
		
		public static final class ApplyActions
		{
			List<Action> action;
			
			public List<Action> getAction() {
				return action;
			}

			public void setAction(List<Action> action) {
				this.action = action;
			}
			
			public void createAction(FlowConfig odlFlow)
			{
				action = new ArrayList<Action>();
				Action act = new Action();
				act.setOrder(odlFlow.getActionOrder());
				act.createOutputAction(odlFlow);
				
				action.add(act);
				
			}

			public static final class Action
			{
				int order = 0;
				@JsonProperty("output-action")
				private OutputAction outputAction;
				
				public int getOrder() {
					return order;
				}
				public void setOrder(int order) {
					this.order = order;
				}
				
				public OutputAction getOutputAction() {
					return outputAction;
				}
				public void setOutputAction(OutputAction outputAction) {
					this.outputAction = outputAction;
				}
				
				public void createOutputAction(FlowConfig odlFlow)
				{
					outputAction = new OutputAction();
					outputAction.setOutputNode(odlFlow.getOutputNode());
					outputAction.setMaxLength(odlFlow.getMaxLength());
				}
				
				public void updateOutputAction(FlowConfig odlFlow)
				{
					outputAction.setOutputNode(odlFlow.getOutputNode());
					outputAction.setMaxLength(odlFlow.getMaxLength());
				}

				public static final class OutputAction
				{
					@JsonProperty("output-node-connector")
					private String outputNode;
					
					@JsonProperty("max-length")
					private int maxLength;

					public String getOutputNode() {
						return outputNode;
					}

					public void setOutputNode(String outputNode) {
						this.outputNode = outputNode;
					}

					public int getMaxLength() {
						return maxLength;
					}

					public void setMaxLength(int maxLength) {
						this.maxLength = maxLength;
					}
					
				}
				
			}
			
		}
	}
	
}
