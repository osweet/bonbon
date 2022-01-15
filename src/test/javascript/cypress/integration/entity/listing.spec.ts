import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Listing e2e test', () => {
  const listingPageUrl = '/listing';
  const listingPageUrlPattern = new RegExp('/listing(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const listingSample = { name: 'encryption' };

  let listing: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/listings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/listings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/listings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (listing) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/listings/${listing.id}`,
      }).then(() => {
        listing = undefined;
      });
    }
  });

  it('Listings menu should load Listings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('listing');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Listing').should('exist');
    cy.url().should('match', listingPageUrlPattern);
  });

  describe('Listing page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(listingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Listing page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/listing/new$'));
        cy.getEntityCreateUpdateHeading('Listing');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', listingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/listings',
          body: listingSample,
        }).then(({ body }) => {
          listing = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/listings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [listing],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(listingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Listing page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('listing');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', listingPageUrlPattern);
      });

      it('edit button click should load edit Listing page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Listing');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', listingPageUrlPattern);
      });

      it('last delete button click should delete instance of Listing', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('listing').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', listingPageUrlPattern);

        listing = undefined;
      });
    });
  });

  describe('new Listing page', () => {
    beforeEach(() => {
      cy.visit(`${listingPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Listing');
    });

    it('should create an instance of Listing', () => {
      cy.get(`[data-cy="name"]`).type('Digitized Rubber').should('have.value', 'Digitized Rubber');

      cy.get(`[data-cy="description"]`).type('index').should('have.value', 'index');

      cy.get(`[data-cy="price"]`).type('2444').should('have.value', '2444');

      cy.get(`[data-cy="address"]`).type('microchip').should('have.value', 'microchip');

      cy.get(`[data-cy="category"]`).type('alarm Licensed global').should('have.value', 'alarm Licensed global');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        listing = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', listingPageUrlPattern);
    });
  });
});
