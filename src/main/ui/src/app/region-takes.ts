import {TakesColorDistribution} from "./takes-color-distribution";

export interface RegionTakes {
  id: number;
  regionId: number;
  regionName: string;
  userId: number;
  takesColorDistribution: TakesColorDistribution;
  roundColorDistribution: TakesColorDistribution;
}
