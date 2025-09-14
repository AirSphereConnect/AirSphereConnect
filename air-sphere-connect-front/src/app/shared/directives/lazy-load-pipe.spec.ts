import { LazyLoadPipe } from './lazy-load-pipe';

describe('LazyLoadPipe', () => {
  it('create an instance', () => {
    const pipe = new LazyLoadPipe();
    expect(pipe).toBeTruthy();
  });
});
