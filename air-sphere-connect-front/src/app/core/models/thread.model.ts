export interface Thread {
  id: number;
  title: string;
  username: string;
  userId?: number;
  createdAt: Date;
  rubricId: number;
}
