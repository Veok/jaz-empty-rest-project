package rest;

import domain.Car;
import domain.Person;
import domain.services.PersonService;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author L on 11.01.2017.
 */
@Path("/people")
@Stateless
public class PeopleResources {

    private PersonService db = new PersonService();

    @PersistenceContext
    EntityManager entityManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getAll() {
        return entityManager.createNamedQuery("person.all", Person.class).getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Add(Person person) {
        entityManager.persist(person);
        return Response.ok(person.getId()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Person p) {
        Person result = entityManager.createNamedQuery("person.id", Person.class)
                .setParameter("personId", id)
                .getSingleResult();
        if (result == null) {
            return Response.status(404).build();
        }
        p.setName(p.getName());
        p.setSurname(p.getSurname());
        entityManager.persist(result);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") int id) {
        Person result = entityManager.createNamedQuery("person.id", Person.class)
                .setParameter("personId", id)
                .getSingleResult();
        if (result == null) {
            return Response.status(404).build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        Person result = entityManager.createNamedQuery("person.id", Person.class)
                .setParameter("personId", id)
                .getSingleResult();
        if (result == null)
            return Response.status(404).build();
        entityManager.remove(result);
        return Response.ok().build();
    }

    @GET
    @Path("/{personId}/cars")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Car> getCars(@PathParam("personId") int personId) {
        Person result = entityManager.createNamedQuery("person.id", Person.class)
                .setParameter("personId", personId)
                .getSingleResult();
        if (result == null)
            return null;
        return result.getCars();
    }

    @POST
    @Path("/{id}/cars")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCar(@PathParam("id") int personId, Car car) {
        Person result = entityManager.createNamedQuery("person.id", Person.class)
                .setParameter("personId", personId)
                .getSingleResult();
        if (result == null)
            return Response.status(404).build();
        result.getCars().add(car);
        car.setPerson(result);
        entityManager.persist(car);
        return Response.ok().build();
    }
}
