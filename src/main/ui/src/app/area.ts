import {TakesColorDistribution} from "./takes-color-distribution";

export interface Area {
  id: number;
  areaName: string;
  takesColorDistribution: TakesColorDistribution;
  roundColorDistribution: TakesColorDistribution;
}
