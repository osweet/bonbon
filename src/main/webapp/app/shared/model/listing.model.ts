export interface IListing {
  id?: number;
  name?: string | null;
  description?: string | null;
  price?: number | null;
  address?: string | null;
  category?: string | null;
}

export class Listing implements IListing {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public price?: number | null,
    public address?: string | null,
    public category?: string | null
  ) {}
}
